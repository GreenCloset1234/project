package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List; // [추가]
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // (기존 메서드 - 채팅방 생성 시 중복 확인용)
    Optional<ChatRoom> findByProduct_ProductIdAndBuyer_UserId(Long productId, Long buyerId);

    /**
     * [신규] 내가 참여하고 있는 모든 채팅방 조회 (내가 구매자 또는 판매자)
     * (Product의 User(판매자) ID 또는 buyer ID가 나 자신인 경우)
     */
    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.buyer.userId = :userId OR cr.product.user.userId = :userId")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);
}