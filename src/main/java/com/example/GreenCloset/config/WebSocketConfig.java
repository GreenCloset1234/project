package com.example.GreenCloset.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. WebSocket 연결 엔드포인트 설정
        // 프론트엔드가 SockJS로 이 주소에 연결을 시도합니다.
        registry.addEndpoint("/ws/chat")

                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS(); // SockJS 지원
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 3. 메시지 브로커 설정

        // "/sub"로 시작하는 주소를 구독(sub)하는 클라이언트에게 메시지 전달
        registry.enableSimpleBroker("/sub");

        // "/pub"로 시작하는 주소로 발행(pub)된 메시지를 컨트롤러(@MessageMapping)로 라우팅
        registry.setApplicationDestinationPrefixes("/pub");
    }
}