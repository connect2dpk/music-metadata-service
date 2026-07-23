package com.deepak.music.common.security.dto;

public record LoginResponse(
        String token,
        String tokenType,
        String username
) {
    public LoginResponse(String token, String username) {
        this(token, "Bearer", username);
    }
}

