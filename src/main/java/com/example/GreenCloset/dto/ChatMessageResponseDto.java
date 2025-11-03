package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor    // [수정] @Builder를 위해 추가
@AllArgsConstructor // [수정] @Builder를 위해 추가
@Builder            // [수정] 클래스 레벨로 이동
public class ChatMessageResponseDto {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderName; // [수정] 발신자 닉네임 필드 추가
    private String content;
    private LocalDateTime sentAt; // (주석 수정: 메시지 발신 시각)

    // (기존의 @Builder가 붙은 생성자 삭제 -> @AllArgsConstructor로 대체)

    public static ChatMessageResponseDto fromEntity(ChatMessage message) {
        if (message == null) {
            return null;
        }

        // [수정] NullPointerException(NPE) 방지를 위한 Null-Safe 로직
        User sender = message.getSender();
        ChatRoom room = message.getChatRoom();

        Long senderId = (sender != null) ? sender.getUserId() : null;
        String senderName = (sender != null) ? sender.getNickname() : "알 수 없는 사용자";
        Long roomId = (room != null) ? room.getRoomId() : null;

        return ChatMessageResponseDto.builder()
                .messageId(message.getMessageId())
                .roomId(roomId) // [수정] Null-Safe 변수 사용
                .senderId(senderId) // [수정] Null-Safe 변수 사용
                .senderName(senderName) // [수정] 누락되었던 senderName 추가
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}