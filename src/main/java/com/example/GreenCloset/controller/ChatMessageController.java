package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatRequestDto;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.service.ChatMessageService; // 이미 가지고 계신 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor; // [추가]
import org.springframework.security.core.Authentication; // [추가]
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller; // 1. @RestController가 아닌 @Controller 사용
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody; // 2. REST API에만 @ResponseBody 추가

import java.util.List;
import java.util.Objects; // [추가]

@Controller // 1. STOMP(@MessageMapping)와 REST(@GetMapping)를 함께 쓰기 위해 @Controller 사용
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService; // (보내주신 Service) [cite: 47-97]
    private final SimpMessagingTemplate messagingTemplate; // 메시지 방송(Broadcast) 도구

    /**
     * [역할 1: REST API]
     * 채팅방의 이전 메시지 내역 조회 (GET /api/v1/chats/{roomId}/messages)
     *
     */
    @GetMapping("/api/v1/chats/{roomId}/messages")
    @ResponseBody // 2. REST API이므로 @ResponseBody를 붙여 JSON으로 응답
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user // REST API는 기존 방식 사용 (정상)
    ) {
        // ChatMessageService의 getMessagesByRoomId 메서드 호출
        List<ChatMessageResponseDto> messages = chatMessageService.getMessagesByRoomId(roomId, user);
        return ResponseEntity.ok(messages);
    }

    /**
     * [역할 2: WebSocket 메시지 처리]
     * 실시간 메시지 전송 및 저장 (Pub /pub/chats/{roomId})
     * [수정] @AuthenticationPrincipal 대신 StompHeaderAccessor에서 직접 인증 정보 추출
     * 이 메서드가 없었기 때문에 DB 저장과 UI 표시가 모두 실패한 것입니다.
     */
    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatRequestDto messageDto,
            StompHeaderAccessor accessor // [수정]
    ) {
        // StompHandler가 세션에 저장한 인증 정보(userAuth)를 꺼냅니다.
        Authentication auth = (Authentication) Objects.requireNonNull(
                accessor.getSessionAttributes()
        ).get("userAuth");

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증된 사용자 정보를 STOMP 세션에서 찾을 수 없습니다.");
        }

        // 인증 객체(Authentication)에서 실제 User 객체를 추출합니다.
        User sender = (User) auth.getPrincipal();

        // 1. Service 호출 (DB 저장)
        ChatMessageResponseDto responseDto = chatMessageService.saveMessage(
                roomId,
                sender.getUserId(),
                messageDto.getContent()
        );

        // 2. /sub/chats/{roomId} [cite: 183] 를 구독 중인 모든 클라이언트에게 메시지 방송 (UI 표시 문제 해결)
        messagingTemplate.convertAndSend("/sub/chats/" + roomId, responseDto);
    }
}