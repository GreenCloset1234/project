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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

// OncePerRequestFilter: 모든 요청마다 이 필터가 딱 한 번씩 실행됨
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청의 쿠키에서 토큰 가져오기
        String token = jwtUtil.getTokenFromCookie(request);

        // 2. 토큰이 존재하고 유효한지 검사
        if (token != null && jwtUtil.validateToken(token)) {
            // 3. 토큰에서 이메일(Subject) 추출
            String email = jwtUtil.getEmailFromToken(token);

            // 4. 이메일로 DB에서 유저 정보 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("인증 실패: 사용자를 찾을 수 없습니다."));

            // 5. Spring Security 인증 토큰 생성
            // (UserDetails 대신 User 객체의 email과 빈 권한 목록을 사용)
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(), "", new ArrayList<>()
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // 6. SecurityContext에 인증 정보 저장
            // (이 작업이 완료되면, 이 요청은 "인증된" 요청으로 처리됨)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}