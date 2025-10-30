package com.example.GreenCloset.repository;


import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // ChatMessageService가 사용
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);
}
