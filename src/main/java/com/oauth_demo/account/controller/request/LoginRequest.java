package com.oauth_demo.account.controller.request;


public record LoginRequest(
    String email,
    String password
) {
}
