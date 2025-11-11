package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // [추가]

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // (기존 메서드) 특정 채팅방 메시지 내역 조회
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(Long roomId);

    /**
     * [신규] 특정 채팅방의 마지막 메시지 1건을 조회
     */
    Optional<ChatMessage> findFirstByChatRoom_RoomIdOrderBySentAtDesc(Long roomId);
}