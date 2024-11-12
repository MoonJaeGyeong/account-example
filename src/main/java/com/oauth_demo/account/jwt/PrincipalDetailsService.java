package com.oauth_demo.account.jwt;

import com.oauth_demo.account.Account;
import com.oauth_demo.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("없어요 유저"));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getMemberRole().name());

        PrincipalDetails userDetails = new PrincipalDetails(account, Collections.singleton(grantedAuthority));

        return userDetails;
    }
}
