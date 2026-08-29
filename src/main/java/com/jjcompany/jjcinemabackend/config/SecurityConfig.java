package com.jjcompany.jjcinemabackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {
    //어떤 요청을 막고 어떤 요청을 열어 둘지 정하는 핵심 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/movies/**", "/api/showtimes/**",
                                "/api/genres/**", "/api/ratings/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/movies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/genres").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/genres/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/ratings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/ratings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/showtimes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/showtimes/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    //로그인 컨트롤러에서 "이메일/비번이 맞는지" 검증할 때 쓸 매니저.
    //내부적으로 CustomUserDetailsService + PasswordEncoder를 자동으로 찾아서 조합해줌
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    //로그인 성공 후 "인증된 상태"를 HTTP 세션에 저장/조회하는 도구
    @Bean
    public SecurityContextRepository securityContextRepository(){
        return new HttpSessionSecurityContextRepository();
    }

}
