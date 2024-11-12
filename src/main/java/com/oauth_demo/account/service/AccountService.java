package com.oauth_demo.account.service;

import com.oauth_demo.account.Account;
import com.oauth_demo.account.controller.request.JoinRequest;
import com.oauth_demo.account.controller.request.LoginRequest;
import com.oauth_demo.account.controller.request.UpdateRequest;
import com.oauth_demo.account.jwt.JwtTokenUtil;
import com.oauth_demo.account.jwt.response.TokenResponse;
import com.oauth_demo.account.repository.AccountRepository;
import com.oauth_demo.account.service.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public MemberResponse join(JoinRequest request) {

        Account account = request.toEntity();
        if (account.isOriginalMember()) {
            account.encodePassoword(passwordEncoder);
        }
        accountRepository.save(account);
        return MemberResponse.from(account);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("없어요"));

        passwordEncoder.matches(account.getPassword(), request.password());
        return jwtTokenUtil.createToken(account.getEmail());
    }

    public MemberResponse update(Account account, UpdateRequest request) {
        account.updateName(request.name());
        return MemberResponse.from(account);
    }
}
