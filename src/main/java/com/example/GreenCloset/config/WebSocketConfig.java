package com.example.GreenCloset.config; // 패키지 경로는 프로젝트에 맞게 확인하세요.

import com.example.GreenCloset.global.StompHandler; // StompHandler 위치
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 웹소켓 메시지 브로커를 활성화합니다.
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler; // JWT 인증 등을 처리할 핸들러

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 설정

        // 1. 클라이언트에게 메시지를 보낼 때 (Subscribe)
        //    /topic (1:N, 채팅방 등)
        //    /queue (1:1, 개인 알림 등)
        registry.enableSimpleBroker("/topic", "/queue");

        // 2. 클라이언트에서 서버로 메시지를 보낼 때 (Publish)
        //    /app 으로 시작하는 주소로 메시지를 보내면, @MessageMapping이 붙은 메서드로 라우팅됩니다.
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 연결 엔드포인트 설정
        // 클라이언트가 /ws 경로로 STOMP 연결을 시도합니다.
        registry.addEndpoint("/ws") // (예: ws://localhost:8080/ws)
                .setAllowedOriginPatterns("*") // CORS 설정
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저를 위한 SockJS 활성화
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // StompHandler(인터셉터)를 등록하여
        // 웹소켓 연결 시 JWT 검증 등의 로직을 처리합니다.
        registration.interceptors(stompHandler);
    }
}