package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // (기존 메서드 - 채팅방 생성 시 중복 확인용)
    Optional<ChatRoom> findByProduct_ProductIdAndBuyer_UserId(Long productId, Long buyerId);

    /**
     * [수정됨] N+1 문제 해결을 위해 Fetch Join 쿼리로 변경
     * (ChatRoom, Product, Product의 User(판매자), ChatRoom의 User(구매자)를 한 번에 로딩)
     */
    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.product p " +
            "JOIN FETCH p.user " + // 판매자
            "JOIN FETCH cr.buyer " + // 구매자
            "WHERE p.user.userId = :userId OR cr.buyer.userId = :userId")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);
}