package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.User;
import com.jjcompany.jjcinemabackend.dto.request.SignupRequest;
import com.jjcompany.jjcinemabackend.dto.response.SignupResponse;
import com.jjcompany.jjcinemabackend.repository.UserRepository;
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

    @jakarta.transaction.Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        User user = User.create(request.email(), passwordEncoder.encode(request.password()), request.name());

        return SignupResponse.from(userRepository.save(user));
    }
}
