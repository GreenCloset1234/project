package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.dto.ChatMessageRequestDto;
import com.example.GreenCloset.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor

public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate; // 메시지 브로드캐스팅용

    /**
     * 실시간 메시지 전송 (WebSocket /pub/chats/{roomId} 엔드포인트)
     */
    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequestDto messageDto,
            Principal principal // (WebSocket 인증을 통해 유저 정보 가져오기)
    ) {
        // (TODO: 임시 ID. 추후 principal 객체에서 실제 유저 ID를 파싱해야 함)
        // String username = principal.getName(); // (email 등)
        // User user = userRepository.findByEmail(username).get();
        // Long senderId = user.getUserId();
        Long senderId = 1L;

        // 1. Service를 호출하여 메시지를 DB에 저장
        ChatMessage savedMessage = chatMessageService.saveMessage(roomId, senderId, messageDto.getContent());

        // 2. DTO로 변환
        ChatMessageResponseDto responseDto = new ChatMessageResponseDto(savedMessage);

        // 3. 해당 채팅방을 구독(/sub/chats/{roomId}) 중인 모든 클라이언트에게 메시지 브로드캐스팅
        messagingTemplate.convertAndSend("/sub/chats/" + roomId, responseDto);
    }
}


