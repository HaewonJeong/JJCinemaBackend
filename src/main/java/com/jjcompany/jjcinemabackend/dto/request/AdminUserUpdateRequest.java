package com.jjcompany.jjcinemabackend.dto.request;

public record AdminUserUpdateRequest(
        String role,
        Boolean active
) {
}
