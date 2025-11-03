package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.User; // User 엔티티 import 필요
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // @AllArgsConstructor 대신 @Builder 사용
public class UserResponseDto {

    private Long userId;
    private String nickname;

    /**
     * User 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static UserResponseDto fromEntity(User user) {
        // user가 null일 경우를 대비한 방어 코드
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }
}