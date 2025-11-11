package com.example.GreenCloset.global;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.jwt.JwtUtil;
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
import org.springframework.stereotype.Component;

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
                accessor.setUser(authentication);
                log.info("STOMP user connected: {}", user.getEmail());

            } else {
                log.warn("STOMP connection refused: Invalid JWT token");
                throw new SecurityException("Invalid JWT token");
            }
        }
        return message;
    }
}