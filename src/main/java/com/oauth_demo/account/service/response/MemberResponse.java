package com.oauth_demo.account.service.response;

import com.oauth_demo.account.Account;
import com.oauth_demo.account.constant.Gender;

public record MemberResponse(
    Long memberId,
    String email,
    String name,
    String phoneNumber,
    Gender gender
) {
    public static MemberResponse from(Account account) {
        return new MemberResponse(account.getId(),
                account.getEmail(),
                account.getName(),
                account.getPhoneNumber(),
                account.getGender());
    }
}
