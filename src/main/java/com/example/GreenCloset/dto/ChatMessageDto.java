package com.example.GreenCloset.dto; // 패키지 경로는 프로젝트에 맞게 확인하세요.

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {

    // 메시지 타입 : 입장(JOIN), 채팅(CHAT), 퇴장(LEAVE)
    public enum MessageType {
        JOIN,
        CHAT,
        LEAVE
    }

    private MessageType type;   // 메시지 타입
    private Long roomId;    // 채팅방 ID (어느 방으로 보낼지)
    private String sender;    // 보낸 사람 (닉네임 또는 사용자 ID)
    private String content;   // 메시지 내용

}