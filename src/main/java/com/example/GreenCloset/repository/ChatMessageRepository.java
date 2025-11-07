package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * [수정] 특정 채팅방의 메시지 내역을 '보낸 시간(sentAt)' 오름차순(Asc)으로 정렬하여 조회
     * (이 메서드를 추가하면 ChatMessageService의 오류가 해결됩니다.)
     */
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(Long roomId);

}