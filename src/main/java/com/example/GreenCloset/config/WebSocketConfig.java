package com.example.GreenCloset.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 메시지 브로커를 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 클라이언트가 WebSocket 연결을 시작할 엔드포인트
        // (API 명세서 6. WebSocket Endpoint: /ws)
        registry.addEndpoint("/ws")
                // (TODO: 추후 배포 시 실제 프론트엔드 URL로 변경해야 함)
                .setAllowedOrigins("http://localhost:3000", "http://localhost:8080")
                .withSockJS(); // (구형 브라우저 호환성을 위해 SockJS 사용)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. 클라이언트가 구독(Subscribe)할 경로의 접두사
        // (API 명세서 6.2. Subscribe: /sub/chats/{roomId})
        registry.enableSimpleBroker("/sub");

        // 3. 클라이언트가 메시지를 발행(Publish)할 경로의 접두사
        // (API 명세서 6.1. Publish: /pub/chats/{roomId})
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // (TODO: WebSocket 연결 시 JWT 인증을 처리하기 위해
    //  StompHeaderAccessor를 가로채는 ChannelInterceptor를 여기에 추가 구현해야 함)
}
