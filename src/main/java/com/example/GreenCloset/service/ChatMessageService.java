package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.repository.ChatMessageRepository;
import com.example.GreenCloset.repository.ChatRoomRepository;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 메시지 저장 (WebSocket Controller에서 호출)
     */
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다."));

        // (TODO: 이 sender가 채팅방의 참여자(구매자 or 판매자)가 맞는지 검증하는 로직 필요)

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();

        return chatMessageRepository.save(message);
    }

    /**
     * 이전 메시지 조회 (GET /chats/{roomId}/messages)
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessagesByRoomId(Long roomId, Long userId) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // (TODO: userId가 이 채팅방의 참여자가 맞는지 검증하는 보안 로직 필요)
        // if (!chatRoom.getBuyer().getUserId().equals(userId) && !chatRoom.getProduct().getUser().getUserId().equals(userId)) {
        //     throw new SecurityException("채팅방에 접근할 권한이 없습니다.");
        // }

        // (이 기능을 위해 ChatMessageRepository 수정이 필요합니다 -> 아래 "다음 단계" 참고)
        return chatMessageRepository.findAllByChatRoom(chatRoom);
    }
}