package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatRoomResponseDto;
// [수정] import 구문 추가
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.ChatRoomRepository;
import com.example.GreenCloset.repository.ProductRepository;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createOrGetChatRoom(Long productId, Long buyerId) {
        // [수정] 이 메서드가 작동하려면 ChatRoomRepository에 메서드 정의가 필요 (아래 4번 참고)
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProduct_ProductIdAndBuyer_UserId(productId, buyerId);

        if (existingRoom.isPresent()) {
            return existingRoom.get().getRoomId();
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (product.getUser().getUserId().equals(buyerId)) {
            // [수정] ErrorCode를 CANNOT_TRADE_OWN_PRODUCT로 변경 (ErrorCode.java에 있어야 함)
            throw new CustomException(ErrorCode.CANNOT_TRADE_OWN_PRODUCT);
        }

        ChatRoom newChatRoom = ChatRoom.builder()
                .product(product)
                .buyer(buyer)
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
        return savedChatRoom.getRoomId();
    }

    public List<ChatRoomResponseDto> getMyChatRooms(User user) {
        Long currentUserId = user.getUserId();

        // [수정] 이 메서드가 작동하려면 ChatRoomRepository에 메서드 정의가 필요 (아래 4번 참고)
        List<ChatRoom> chatRooms = chatRoomRepository.findByBuyer_UserIdOrProduct_User_UserId(currentUserId, currentUserId);

        return chatRooms.stream()
                .map(ChatRoomResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}