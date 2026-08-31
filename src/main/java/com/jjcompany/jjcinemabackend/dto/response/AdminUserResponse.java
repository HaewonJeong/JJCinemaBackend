package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.User;

public record AdminUserResponse(
        Long id,
        String name,
        String email,
        String role,
        Boolean active
) {
    public static AdminUserResponse from(User user){
        return new AdminUserResponse(
                user.getUserId(), user.getName(), user.getEmail(), user.getRole(), user.getActive());
    }
}
