package com.example.GreenCloset.config;

import com.example.GreenCloset.jwt.JwtAuthenticationFilter;
import com.example.GreenCloset.jwt.JwtUtil;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // [추가] HttpMethod 임포트
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // [추가] CORS 임포트
import org.springframework.web.cors.CorsConfigurationSource; // [추가] CORS 임포트
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // [추가] CORS 임포트

import java.util.Arrays; // [추가] Arrays 임포트
import java.util.List; // [추가] List 임포트

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userRepository);
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // ★ 1. CORS 설정 Bean (이미지 업로드 오류 해결) ★
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 개발 서버 주소 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 모든 HTTP 메서드 (GET, POST, PUT, DELETE, OPTIONS 등) 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 모든 HTTP 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));

        // 자격 증명(쿠키, 인증 헤더) 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 이 CORS 설정을 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
                // ★ 2. CORS 설정을 SecurityFilterChain에 적용 ★
                // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
                        // ★ 3. OPTIONS "사전 요청"은 무조건 허용 ★
                        // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/v1/users/signup").permitAll()
                        .requestMatchers("/api/v1/users/login").permitAll()
                        .requestMatchers("/api/v1/products/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}