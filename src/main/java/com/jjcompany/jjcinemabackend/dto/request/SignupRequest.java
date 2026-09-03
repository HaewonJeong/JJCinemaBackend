package com.jjcompany.jjcinemabackend.dto.request;

import jakarta.validation.constraints.NotBlank;

// SignupRequest.java - 클라이언트가 회원가입할 때 보내는 JSON을 받는 그릇
public record SignupRequest(
        @NotBlank(message = "이메일은 필수입니다.") String email,
        @NotBlank(message = "비밀번호는 필수입니다.") String password,
        @NotBlank(message = "이름은 필수입니다.") String name
){}
