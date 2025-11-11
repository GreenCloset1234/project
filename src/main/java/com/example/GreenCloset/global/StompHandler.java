package com.example.GreenCloset.global; // (StompHandler 경로)

import com.example.GreenCloset.jwt.JwtUtil; // (JwtUtil 경로)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // [수정] Slf4j, log 오류 해결
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // [수정]
import org.springframework.security.core.Authentication; // [수정]
import org.springframework.stereotype.Component;

@Slf4j // [수정] log 변수 사용을 위해
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 1. WebSocket 연결 요청(CONNECT)일 때만 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // 2. Authorization 헤더에서 토큰 추출
            String authToken = accessor.getFirstNativeHeader("Authorization");

            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            // 3. [수정] '태정'님의 JwtUtil.validateToken() 사용
            if (authToken != null && jwtUtil.validateToken(authToken)) {

                // 4. [수정] 'getAuthentication' 오류 해결
                // '태정'님의 JwtUtil.getEmailFromToken()을 사용
                String email = jwtUtil.getEmailFromToken(authToken);

                // 5. [수정] email로 Authentication 객체 생성
                // (Spring Security가 Principal을 인식할 수 있도록)
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, null);
                accessor.setUser(authentication);
                log.info("STOMP user connected: {}", authentication.getName());

            } else {
                log.warn("STOMP connection refused: Invalid JWT token");
                throw new SecurityException("Invalid JWT token");
            }
        }
        return message;
    }
}