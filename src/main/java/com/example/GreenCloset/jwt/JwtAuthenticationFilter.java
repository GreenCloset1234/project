package com.example.GreenCloset.jwt;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // [수정] DB에서 실제 유저 정보를 조회하기 위해 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검증
        if (token != null && jwtUtil.validateToken(token)) {
            // 3. 토큰에서 이메일(Subject) 추출
            String email = jwtUtil.getEmailFromToken(token);

            // 4. 이메일로 DB에서 User 정보 조회
            User user = userRepository.findByEmail(email)
                    .orElse(null); // (실제로는 CustomException 사용 권장)

            if (user != null) {
                // 5. 인증 정보(Authentication) 객체 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, // Principal (인증된 사용자 객체)
                        null, // Credentials (비밀번호 - null 처리)
                        null  // Authorities (권한 - 여기서는 생략)
                );
                // 6. SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // "Authorization" 헤더에서 "Bearer " 접두사를 제거하고 토큰만 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
