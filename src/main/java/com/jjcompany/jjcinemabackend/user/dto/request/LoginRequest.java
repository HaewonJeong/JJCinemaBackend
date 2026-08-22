package com.jjcompany.jjcinemabackend.user.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
