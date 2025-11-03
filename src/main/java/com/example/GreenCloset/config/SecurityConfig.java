package com.example.GreenCloset.config;

// (필요한 import 구문들)
import com.example.GreenCloset.jwt.JwtAuthenticationFilter;
import com.example.GreenCloset.jwt.JwtUtil;
import com.example.GreenCloset.repository.UserRepository; // (필터가 UserRepository를 사용하므로 import)
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    // [수정] JwtUtil과 UserRepository 주입
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 1. PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 2. AuthenticationManager Bean 등록 (가장 중요!)
     * - UserService가 'bean을 찾을 수 없습니다' 오류가 뜬 이유
     * - 이 Bean을 등록해야 @RequiredArgsConstructor로 주입받을 수 있습니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 3. JwtAuthenticationFilter Bean 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // [수정] filter가 UserRepository를 사용하도록 전달
        return new JwtAuthenticationFilter(jwtUtil, userRepository);
    }

    // 4. SecurityFilterChain Bean 등록 (메인 설정)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함

                // 엔드포인트별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/signup").permitAll() // 회원가입
                        .requestMatchers("/api/v1/users/login").permitAll()  // 로그인
                        .requestMatchers("/api/v1/products/**").permitAll() // 상품 조회
                        .requestMatchers("/ws/**").permitAll() // WebSocket
                        .anyRequest().authenticated() // 나머지 모든 요청은 인증 필요
                );

        // [수정] Bean으로 등록한 jwtAuthenticationFilter()를 사용
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}