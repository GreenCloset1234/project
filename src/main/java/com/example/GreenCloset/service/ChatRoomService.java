package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatRoomCreateRequestDto;
import com.example.GreenCloset.dto.ChatRoomListResponseDto;
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.ChatMessageRepository;
import com.example.GreenCloset.repository.ChatRoomRepository;
import com.example.GreenCloset.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // [수정] Optional, isPresent, get 오류 해결
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅방 생성 (또는 기존 채팅방 조회)
     */
    @Transactional
    public Long createOrGetChatRoom(ChatRoomCreateRequestDto requestDto, User buyer) {
        Long productId = requestDto.getProductId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getUser().getUserId().equals(buyer.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "자신의 상품에는 채팅방을 개설할 수 없습니다.");
        }

        // 1. 기존 채팅방이 있는지 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProduct_ProductIdAndBuyer_UserId(productId, buyer.getUserId());

        if (existingRoom.isPresent()) { // [수정] 오류 해결
            return existingRoom.get().getRoomId(); // [수정] 오류 해결
        } else {
            // 3. 없으면 새로 생성
            ChatRoom newChatRoom = ChatRoom.builder()
                    .product(product)
                    .buyer(buyer)
                    .build();
            ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
            return savedChatRoom.getRoomId();
        }
    }

    /**
     * [신규] 내 채팅방 목록 조회 (GET /api/v1/chats)
     * (이 메서드가 "1-arg, 2-found" 오류의 원인이었을 수 있습니다)
     */
    public List<ChatRoomListResponseDto> getMyChatRooms(User user) {
        Long myUserId = user.getUserId();

        // 1. 내가 참여중인 모든 채팅방 조회 (구매자 or 판매자)
        List<ChatRoom> myRooms = chatRoomRepository.findChatRoomsByUserId(myUserId);

        // 2. DTO로 변환
        return myRooms.stream().map(room -> {
            // 3. 상대방 정보 찾기
            User seller = room.getProduct().getUser();
            User buyer = room.getBuyer();
            User partner = myUserId.equals(seller.getUserId()) ? buyer : seller;

            // 4. 마지막 메시지 찾기
            String lastMessage = chatMessageRepository.findFirstByChatRoom_RoomIdOrderBySentAtDesc(room.getRoomId())
                    .map(ChatMessage::getContent) // (메시지가 있으면 content 반환)
                    .orElse("대화 내역이 없습니다."); // (메시지가 없으면 기본값)

            // 5. 최종 DTO 생성 (ChatRoomListResponseDto의 fromEntity 호출)
            return ChatRoomListResponseDto.fromEntity(room, partner, lastMessage);
        }).collect(Collectors.toList());
    }
}