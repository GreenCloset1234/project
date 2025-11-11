package com.example.GreenCloset.config;

import com.example.GreenCloset.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // HttpMethod 임포트
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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화

                // 세션 정책: 상태 없음(Stateless)으로 설정 (JWT 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // HTTP 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize

                        // [핵심] WebSocket 연결 경로(/ws/**)는 누구나 접근 가능하도록 허용
                        .requestMatchers("/ws/**").permitAll()

                        // 로그인, 회원가입 API는 누구나 접근 가능
                        .requestMatchers("/api/v1/login", "/api/v1/signup").permitAll()

                        // [참고] 상품 조회, 프로필 조회 등 GET 요청은 누구나 접근 가능
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/products",
                                "/api/v1/products/**",
                                "/api/v1/products/users/**",
                                "/api/v1/users/{userId}"
                        ).permitAll()

                        // 위에서 허용한 경로를 제외한 "나머지 모든 요청"은 "인증(authenticated)" 필요
                        .anyRequest().authenticated()
                )

                // Spring Security 필터 체인에 JwtAuthenticationFilter를 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}