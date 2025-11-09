package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.dto.ChatRequestDto;
import com.example.GreenCloset.repository.UserRepository;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatRequestDto messageDto,
            Principal principal
    ) {
        // 1. Principal(인증 정보)에서 사용자 이메일(이름)을 가져옴
        String email = principal.getName();

        // 2. 이메일로 User 객체 조회
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));
        Long senderId = sender.getUserId();

        // 3. [수정] Service를 호출하여 DTO를 "직접" 반환받습니다.
        //    (Service가 이미 DTO 변환까지 완료해서 반환해줍니다.)
        ChatMessageResponseDto responseDto = chatMessageService.saveMessage(roomId, senderId, messageDto.getContent());

        // 4. [삭제] DTO로 변환하는 단계가 필요 없어졌습니다.
        // ChatMessage savedMessage = ... (X)
        // ChatMessageResponseDto responseDto = ChatMessageResponseDto.fromEntity(savedMessage); (X)

        // 5. 해당 채팅방 구독자에게 DTO 메시지 브로드캐스팅
        messagingTemplate.convertAndSend("/sub/chats/" + roomId, responseDto);
    }
}