package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.User;

public record UserResponse (
    Long id,
    String name,
    String email,
    String role
){
    public static UserResponse from(User user){
        return new UserResponse(user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }
}
