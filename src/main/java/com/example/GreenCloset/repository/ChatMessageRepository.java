package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // [추가]

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // (기존) 특정 채팅방의 모든 메시지 조회 (오름차순)
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(Long roomId);

    /**
     * [신규 추가] 특정 채팅방의 가장 최근 메시지 1건 조회 (내림차순)
     * ChatRoomService에서 채팅방 목록의 '마지막 메시지'를 표시하기 위해 필요합니다.
     */
    Optional<ChatMessage> findFirstByChatRoom_RoomIdOrderBySentAtDesc(Long roomId);
}