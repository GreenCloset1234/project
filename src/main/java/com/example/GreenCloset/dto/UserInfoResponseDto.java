package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.User; // User 엔티티 import 필요
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // @AllArgsConstructor 대신 @Builder 사용
public class UserInfoResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String introduction;

    /**
     * User 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static UserInfoResponseDto fromEntity(User user) {
        // user가 null일 경우를 대비한 방어 코드
        if (user == null) {
            return null;
        }

        return UserInfoResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl()) // (User 엔티티에 필드가 있다고 가정)
                .introduction(user.getIntroduction())     // (User 엔티티에 필드가 있다고 가정)
                .build();
    }
}