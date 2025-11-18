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
    private final UserRepository userRepository; // DB 조회를 위해 주입

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 요청 처리 (기존 로직 보강)
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            if (authToken != null && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            if (authToken != null && jwtUtil.validateToken(authToken)) {
                String email = jwtUtil.getEmailFromToken(authToken);
                // email로 User 객체를 DB에서 조회
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new SecurityException("User not found from token"));

                // [수정] 'getAuthorities' 오류 해결
                // Principal을 User 객체로 설정하고, authorities는 null로 전달
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, null);
                // (2) [중요] STOMP 세션 속성에 인증 정보 저장
                Objects.requireNonNull(accessor.getSessionAttributes()).put("userAuth", authentication);
                // (3) accessor에도 User 설정 (CONNECT 메시지 자체에도 인증 설정)
                accessor.setUser(authentication);
                log.info("STOMP user connected: {}", user.getEmail());

            } else {
                log.warn("STOMP connection refused: Invalid JWT token");
                throw new SecurityException("Invalid JWT token");
            }
            // 2. PUBLISH (메시지 전송) 요청 처리
        } else if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // (1) [중요] 세션 속성에서 저장된 인증 정보 꺼내기
            Authentication authentication = (Authentication) Objects.requireNonNull(accessor.getSessionAttributes()).get("userAuth");

            if (authentication != null) {
                // (2) [핵심] SecurityContextHolder에 인증 정보 설정
                //      이것으로 @AuthenticationPrincipal이 null이 되는 것을 방지
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // (3) accessor에도 User 설정
                accessor.setUser(authentication);
            } else {
                log.warn("STOMP PUBLISH GUESSED: No authentication found in session");
                // (인증 정보가 없으면 컨트롤러에서 null이 됨)
                throw new SecurityException("No authentication in session. Reconnect required.");
            }
        }

        // (SUBSCRIBE, DISCONNECT 등 나머지 명령은 그냥 통과)
        return message;
    }
}