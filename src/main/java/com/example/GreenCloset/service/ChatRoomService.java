package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.repository.ChatRoomRepository;
import com.example.GreenCloset.repository.ProductRepository;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 채팅방 생성/조회 (POST /chats)
     */
    public Long createOrGetChatRoom(Long productId, Long buyerId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("구매자를 찾을 수 없습니다."));

        // 1. 판매자와 구매자가 같은지 확인
        if (product.getUser().getUserId().equals(buyerId)) {
            throw new IllegalArgumentException("자신의 상품에는 채팅방을 개설할 수 없습니다.");
        }

        // 2. 이미 해당 상품/구매자 조합의 채팅방이 있는지 확인
        // (이 기능을 위해 ChatRoomRepository 수정이 필요합니다 -> 아래 "다음 단계" 참고)
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProductAndBuyer(product, buyer);

        if (existingRoom.isPresent()) {
            // 3. 이미 있으면 기존 채팅방 ID 반환
            return existingRoom.get().getRoomId();
        } else {
            // 4. 없으면 새로 생성
            ChatRoom newRoom = ChatRoom.builder()
                    .product(product)
                    .buyer(buyer)
                    .build();

            ChatRoom savedRoom = chatRoomRepository.save(newRoom);
            return savedRoom.getRoomId();
        }
    }

    /**
     * 내 채팅방 목록 조회 (GET /chats)
     */
    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // (이 기능을 위해 ChatRoomRepository 수정이 필요합니다 -> 아래 "다음 단계" 참고)
        // 내가 구매자(buyer)이거나, 내가 상품의 판매자(product.user)인 모든 채팅방 조회
        return chatRoomRepository.findAllByBuyerOrProductUser(user, user);
    }
}