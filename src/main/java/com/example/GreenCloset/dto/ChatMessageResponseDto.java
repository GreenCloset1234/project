package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.User; // [추가]
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private Long messageId;
    private Long senderId;

    // [가이드 2. 로직 3]
    private String senderNickname; // (v2 명세서의 'senderName' 역할)

    // [가이드 2. 로직 3] (이 필드가 누락되었습니다)
    private String senderProfileImageUrl;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    /**
     * [수정] 가이드에 맞게 senderProfileImageUrl 추가
     */
    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        User sender = chatMessage.getSender(); // 보낸 사람(User) 정보

        return ChatMessageResponseDto.builder()
                .messageId(chatMessage.getMessageId())
                .senderId(sender.getUserId())
                .senderNickname(sender.getNickname()) // [가이드 2. 로직 3]
                .senderProfileImageUrl(sender.getProfileImageUrl()) // [가이드 2. 로직 3]
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}