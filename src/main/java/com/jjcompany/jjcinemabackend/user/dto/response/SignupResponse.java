package com.jjcompany.jjcinemabackend.user.dto.response;

import com.jjcompany.jjcinemabackend.user.entity.User;

// SignupResponse.java - 응답으로 내려줄 데이터. 비밀번호는 절대 포함 안 시킴(보안)
public record SignupResponse(Long id, String email, String name) {
    public static SignupResponse from(User user){
        return new SignupResponse(user.getId(), user.getEmail(), user.getName());
    }
}
