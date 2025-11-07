package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String profileImageUrl;

    private String introduction;

    // [수정] "마일리지" 필드 제거
    // @Column(columnDefinition = "BIGINT default 0")
    // private Long mileage;

    /**
     * 비밀번호 변경 메서드
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 프로필 이미지 변경 메서드
     */
    public void updateProfileImage(String profileImageKey) {
        this.profileImageUrl = profileImageKey;
    }
}