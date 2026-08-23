package com.jjcompany.jjcinemabackend.dto.request;

// SignupRequest.java - 클라이언트가 회원가입할 때 보내는 JSON을 받는 그릇
public record SignupRequest(String email, String password, String name){}
