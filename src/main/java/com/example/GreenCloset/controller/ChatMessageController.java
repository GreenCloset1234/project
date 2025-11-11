package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.dto.ChatRequestDto;
import com.example.GreenCloset.repository.UserRepository;
import com.example.GreenCloset.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // [추가]
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // [추가]
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping; // [추가]
import org.springframework.web.bind.annotation.PathVariable; // [추가]
import org.springframework.web.bind.annotation.RequestMapping; // [추가]
import org.springframework.web.bind.annotation.RestController; // [추가]

import java.security.Principal;
import java.util.List; // [추가]

@RestController // [수정] @Controller -> @RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    /**
     * [신규] 500 오류 해결: 이전 대화 내역 조회 (REST API)
     * GET /api/v1/chats/{roomId}/messages
     */
    @GetMapping("/api/v1/chats/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user // (인증된 사용자)
    ) {
        List<ChatMessageResponseDto> messages = chatMessageService.getMessagesByRoomId(roomId, user);
        return ResponseEntity.ok(messages);
    }

    /**
     * 실시간 메시지 전송 (WebSocket)
     * @MessageMapping("/pub/chats/{roomId}")
     */
    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatRequestDto messageDto,
            Principal principal // (WebSocket 인증 정보)
    ) {
        String email = principal.getName();
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));
        Long senderId = sender.getUserId();

        ChatMessageResponseDto responseDto = chatMessageService.saveMessage(roomId, senderId, messageDto.getContent());

        messagingTemplate.convertAndSend("/sub/chats/" + roomId, responseDto);
    }
}