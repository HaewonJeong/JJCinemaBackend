package com.jjcompany.jjcinemabackend.user.service;

import com.jjcompany.jjcinemabackend.user.dto.request.SignupRequest;
import com.jjcompany.jjcinemabackend.user.dto.response.SignupResponse;
import com.jjcompany.jjcinemabackend.user.entity.User;
import com.jjcompany.jjcinemabackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        return SignupResponse.from(userRepository.save(user));
    }
}