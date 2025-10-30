package com.example.GreenCloset.repository;


import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // ChatRoomService가 사용
    Optional<ChatRoom> findByProductAndBuyer(Product product, User buyer);
    List<ChatRoom> findAllByBuyerOrProductUser(User buyer, User productUser);
}