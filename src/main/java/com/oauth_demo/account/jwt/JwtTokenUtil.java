package com.oauth_demo.account.jwt;

import com.oauth_demo.account.Account;
import com.oauth_demo.account.repository.AccountRepository;
import com.oauth_demo.account.constant.MemberRole;
import com.oauth_demo.account.jwt.response.TokenResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String COOKIE_REFRESH_TOKEN = "refreshToken";
    public static final long ACCESS_TIME = Duration.ofMinutes(30).toMillis(); // 만료시간 30분
    public static final long REFRESH_TIME = Duration.ofDays(14).toMillis(); // 만료시간 2주

    private final PrincipalDetailsService userDetailService;
    private final RedisTemplate redisTemplate;
    private final AccountRepository accountRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String getHeaderToken(HttpServletRequest request, String type) {
        if (type.equals(ACCESS_TOKEN))
            return resolveToken(request);

        return request.getHeader(REFRESH_TOKEN);
    }

    public TokenResponse createToken(String email) {
        MemberRole role = getRoleFromEmail(email);

        String accessToken = createAccessToken(email, role);
        String refreshToken = createRefreshToken(email);

        return TokenResponse.of(accessToken, refreshToken, REFRESH_TIME);
    }

    public String createAccessToken(String email, MemberRole role) {
        Date date = new Date();


        return Jwts.builder()
            .setSubject(email)
            .claim("role", role.name())
            .setExpiration(new Date(date.getTime() + ACCESS_TIME))
            .setIssuedAt(date)
            .signWith(SignatureAlgorithm.HS256, key)
            .compact();
    }

    public String reCreateAccessToken(String refreshToken) {
        Date date = new Date();
        String email = getEmailFromToken(refreshToken);
        MemberRole role = getRoleFromEmail(email);

        return Jwts.builder()
            .setSubject(email)
            .claim("role", role.name())
            .setExpiration(new Date(date.getTime() + ACCESS_TIME))
            .setIssuedAt(date)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String createRefreshToken(String email) {
        Date date = new Date();

        return Jwts.builder()
            .setSubject(email)
            .setExpiration(new Date(date.getTime() + REFRESH_TIME))
            .setIssuedAt(date)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();

        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    private MemberRole getRoleFromEmail(String email) {
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("없어요 유저"));

        return account.getMemberRole();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(token)) {
            if (token.startsWith(BEARER_PREFIX)) {
                return token.substring(7).trim();
            }
            return token;
        }

        return null;
    }

    public void tokenValidation(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (ExpiredJwtException e) {
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (IllegalArgumentException e) {
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void refreshTokenValidation(String refreshToken) {
        tokenValidation(refreshToken);
        String email = getEmailFromToken(refreshToken);
        String redisRefreshToken = null;

        Object redisRefresh = redisTemplate.opsForValue().get(email);

        if (redisRefresh != null)
            redisRefreshToken = redisRefresh.toString();

        if (redisRefreshToken == null)
            throw new JwtException("유효하지 않은 JWT 토큰");

    }

}