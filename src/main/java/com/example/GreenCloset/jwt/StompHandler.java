package com.example.GreenCloset.jwt;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 1. [CONNECT] 웹소켓 연결 요청 시 -> 토큰 검증 및 세션 저장
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            if (authToken != null && jwtUtil.validateToken(authToken)) {
                String email = jwtUtil.getEmailFromToken(authToken);

                // DB에서 사용자 조회
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new SecurityException("User not found from token"));

                // 인증 객체 생성 (Principal에 User 엔티티 저장)
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, null);

                // [중요] 세션 속성(SessionAttributes)에 인증 정보 저장 -> 나중에 SEND 할 때 꺼내 씀
                Objects.requireNonNull(accessor.getSessionAttributes()).put("userAuth", authentication);

                // 현재 헤더에도 인증 정보 설정
                accessor.setUser(authentication);
                log.info("STOMP Connected: {}", user.getEmail());

            } else {
                log.warn("STOMP Connection Refused: Invalid Token");
                // 연결 거부
                throw new SecurityException("Invalid JWT token");
            }
        }
        // 2. [SEND / SUBSCRIBE] 메시지 전송 또는 구독 요청 시 -> 세션에서 인증 정보 꺼내기
        // [수정 완료] 기존에 CONNECT로 잘못 되어있던 부분을 SEND와 SUBSCRIBE로 변경
        else if (StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            // 세션에 저장해둔 인증 정보 가져오기
            Object authObj = Objects.requireNonNull(accessor.getSessionAttributes()).get("userAuth");

            if (authObj instanceof Authentication) {
                Authentication authentication = (Authentication) authObj;

                // [핵심] SecurityContextHolder에 설정해줘야 Controller에서 @AuthenticationPrincipal로 받을 수 있음
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Accessor에도 설정
                accessor.setUser(authentication);
            } else {
                log.error("STOMP Error: No authentication found in session for command {}", accessor.getCommand());
                // 필요 시 예외 발생: throw new SecurityException("Unauthorized");
            }
        }

        return message;
    }
}