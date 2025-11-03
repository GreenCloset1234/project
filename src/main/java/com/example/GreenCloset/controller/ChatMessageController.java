package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.dto.ChatRequestDto;
import com.example.GreenCloset.repository.UserRepository; // [수정] UserRepository 주입
import com.example.GreenCloset.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal; // (웹소켓 인증용)

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository; // [수정] Principal에서 User를 찾기 위해

    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatRequestDto messageDto, // (WebSocket DTO는 @Valid 지원이 복잡하므로 생략)
            Principal principal // (WebSocketConfig에서 인증 설정 시 주입됨)
    ) {
        // 1. Principal(인증 정보)에서 사용자 이메일(이름)을 가져옴
        String email = principal.getName();

        // 2. 이메일로 User 객체 조회
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));
        Long senderId = sender.getUserId();

        // 3. Service를 호출하여 메시지를 DB에 저장
        ChatMessage savedMessage = chatMessageService.saveMessage(roomId, senderId, messageDto.getContent());

        // 4. DTO로 변환
        ChatMessageResponseDto responseDto = ChatMessageResponseDto.fromEntity(savedMessage);

        // 5. 해당 채팅방 구독자에게 메시지 브로드캐스팅
        messagingTemplate.convertAndSend("/sub/chats/" + roomId, responseDto);
    }
}