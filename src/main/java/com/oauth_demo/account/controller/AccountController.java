package com.oauth_demo.account.controller;

import com.oauth_demo.account.controller.request.JoinRequest;
import com.oauth_demo.account.controller.request.LoginRequest;
import com.oauth_demo.account.controller.request.UpdateRequest;
import com.oauth_demo.account.jwt.PrincipalDetails;
import com.oauth_demo.account.jwt.response.TokenResponse;
import com.oauth_demo.account.service.AccountService;
import com.oauth_demo.account.service.response.MemberResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/join")
    public ResponseEntity<MemberResponse> join(@RequestBody JoinRequest request) {
        return ResponseEntity.ok(accountService.join(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse loginResponse = accountService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/")
    public ResponseEntity<MemberResponse> update(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @RequestBody UpdateRequest request) {

        return ResponseEntity.ok(accountService.update(principalDetails.getAccount(), request));
    }

}
