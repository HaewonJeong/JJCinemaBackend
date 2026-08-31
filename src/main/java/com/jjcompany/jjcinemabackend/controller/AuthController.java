package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.domain.User;
import com.jjcompany.jjcinemabackend.dto.request.LoginRequest;
import com.jjcompany.jjcinemabackend.dto.request.SignupRequest;
import com.jjcompany.jjcinemabackend.dto.response.SignupResponse;
import com.jjcompany.jjcinemabackend.dto.response.UserResponse;
import com.jjcompany.jjcinemabackend.repository.UserRepository;
import com.jjcompany.jjcinemabackend.service.AuthService;
import com.jjcompany.jjcinemabackend.service.GenreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // 이메일/비번 검증 (틀리면 AuthenticationException 발생 -> GlobalExceptionHandler가 401로 처리)
        Authentication authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 인증 성공 정보를 담을 빈 컨텍스트 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        // 지금 이 요청 처리 중에는 "로그인된 상태"로 인식되게 설정
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        // 이걸 안 하면 이번 요청에만 로그인 상태이고, 다음 요청부터는 다시 로그아웃 상태가 됨
        // 여기서 실제로 HTTP 세션(쿠키)에 로그인 상태를 저장함
        User user = userRepository.findByEmail(request.email())
                        .orElseThrow(()-> new IllegalStateException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }
}
