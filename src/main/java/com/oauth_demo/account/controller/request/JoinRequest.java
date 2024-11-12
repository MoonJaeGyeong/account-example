package com.oauth_demo.account.controller.request;


import com.oauth_demo.account.Account;
import com.oauth_demo.account.constant.Gender;
import com.oauth_demo.account.constant.LoginType;
public record JoinRequest(

    String email,
    String password,
    String passwordConfirm,
    String name,
    String phoneNumber,
    String address,
    Gender gender,
    LoginType loginType
) {

    public void checkPassword() {
        if (!password.equals(passwordConfirm))
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
    }

    public Account toEntity() {
        return Account.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .address(address)
                .loginType(loginType)
                .build();
    }
}
