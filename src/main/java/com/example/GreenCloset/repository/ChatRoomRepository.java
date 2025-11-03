package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * [수정] ChatRoomService에서 사용할 메서드 (채팅방 생성/조회 시)
     */
    Optional<ChatRoom> findByProduct_ProductIdAndBuyer_UserId(Long productId, Long buyerId);

    /**
     * [수정] ChatRoomService에서 사용할 메서드 (내 채팅방 목록 조회 시)
     */
    List<ChatRoom> findByBuyer_UserIdOrProduct_User_UserId(Long buyerId, Long sellerId);

}