package com.oauth_demo.account.jwt;

import com.oauth_demo.account.Account;
import com.oauth_demo.account.constant.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    @Getter
    private Account account;
    private Collection<? extends GrantedAuthority> authorities;

    public PrincipalDetails(Account account, Collection<? extends GrantedAuthority> authorities) {
        this.account = account;
        this.authorities = authorities;
    }

    public MemberRole getMemberRole() {
        return account.getMemberRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }


}
