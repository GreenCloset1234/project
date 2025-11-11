package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.dto.ChatRequestDto;
// [수정] UserRepository 임포트 제거 (필요 없음)
import com.example.GreenCloset.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // [중요]
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// [수정] Principal 임포트 제거 (필요 없음)
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // ... (getMessages @GetMapping 메서드) ...
    @GetMapping("/api/v1/chats/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user
    ) {
        List<ChatMessageResponseDto> messages = chatMessageService.getMessagesByRoomId(roomId, user);
        return ResponseEntity.ok(messages);
    }

    /**
     * 실시간 메시지 전송 (WebSocket)
     * (StompHandler가 User 객체를 주입해주므로 이 코드가 정상 작동)
     */
    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatRequestDto messageDto,
            @AuthenticationPrincipal User sender // [정상] User 객체가 주입됨
    ) {
        if (sender == null) {
            // (StompHandler에서 인증 실패 시)
            throw new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        Long senderId = sender.getUserId();

        // [정상] 이 메서드가 이제 호출됩니다.
        ChatMessageResponseDto responseDto = chatMessageService.saveMessage(roomId, senderId, messageDto.getContent());

        messagingTemplate.convertAndSend("/sub/chats/" + roomId, responseDto);
    }
}