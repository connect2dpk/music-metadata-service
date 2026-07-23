package com.deepak.music.common.security.dto;

public record LoginRequest(
        String username,
        String password
) {
}

