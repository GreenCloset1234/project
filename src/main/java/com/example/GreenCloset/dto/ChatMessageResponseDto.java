package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat; // [추가]
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
    private String senderNickname;
    private String content;

    // [수정] Invalid Date 오류 해결
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    /**
     * [수정] fromEntity 인수를 1개(ChatMessage)만 받도록 변경
     * (이것이 500 서버 오류의 원인이었습니다)
     */
    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .messageId(chatMessage.getMessageId())
                .senderId(chatMessage.getSender().getUserId())
                .senderNickname(chatMessage.getSender().getNickname())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}