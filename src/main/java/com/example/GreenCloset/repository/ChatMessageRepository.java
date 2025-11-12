package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * [가이드 3] (문제 2 해결)
     * 특정 채팅방의 메시지 내역 조회 (시간순)
     */
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(Long roomId);

    /**
     * [참고] (GET /chats API가 사용)
     * 특정 채팅방의 마지막 메시지 1건 조회
     */
    Optional<ChatMessage> findFirstByChatRoom_RoomIdOrderBySentAtDesc(Long roomId);
}