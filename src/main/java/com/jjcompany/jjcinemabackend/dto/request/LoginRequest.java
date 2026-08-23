package com.jjcompany.jjcinemabackend.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
