package com.example.GreenCloset.config;

import com.example.GreenCloset.jwt.JwtAuthenticationFilter;
import com.example.GreenCloset.jwt.JwtUtil;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .cors(cors -> {}) // (WebConfig에서 설정했으므로 여기선 기본값 사용)

                // 세션 관리 정책을 STATELESS로 설정 (토큰 기반)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // HTTP 요청 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 1. 인증 없이 접근 허용
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()


                        .requestMatchers("/ws/chat/**").permitAll()

                        // 3. 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 4. JWT 인증 필터를 Spring Security 필터 체인에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}