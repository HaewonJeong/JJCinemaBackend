package com.jjcompany.jjcinemabackend.security;

import com.jjcompany.jjcinemabackend.domain.User;
import com.jjcompany.jjcinemabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                // 이메일로 못 찾으면 이 예외를 던짐 -> Spring Security가 로그인 실패로 처리
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다: " + email));

        // 우리 User 엔티티를 Spring Security가 이해하는 UserDetails 객체로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // 이미 암호화된 상태로 DB에 저장돼 있음
                // role("CUSTOMER"/"ADMIN")을 "ROLE_CUSTOMER" 같은 권한 형태로 변환
                // Spring Security 관례상 권한 문자열 앞에 "ROLE_"을 붙여야 인식함
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .disabled(!user.getActive())
                .build();
    }
}