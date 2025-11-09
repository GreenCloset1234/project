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

    /**
     * 비밀번호 변경 메서드
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 프로필 이미지 변경 메서드
     */
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * [신규] 닉네임, 한줄소개 변경 메서드
     */
    public void updateProfile(String nickname, String introduction) {
        // (닉네임이 null이 아니고 비어있지 않은 경우에만 업데이트)
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        // (한줄소개는 null이거나 빈 값("")으로도 설정 가능하도록 허용)
        if (introduction != null) {
            this.introduction = introduction;
        }
    }
}