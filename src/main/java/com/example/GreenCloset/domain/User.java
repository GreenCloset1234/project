package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder // [추가] UserService의 signup 메서드에서 .builder()를 사용하기 위해
@NoArgsConstructor(access = AccessLevel.PROTECTED) // [추가] JPA는 기본 생성자가 필요
@AllArgsConstructor // [추가] @Builder가 모든 필드 생성자를 사용하기 위해
@Table(name = "users") // (DB 테이블명을 'users'로 명시)
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

    // (TODO: Enum 타입의 UserRole(권한) 필드 추가 권장)
    // @Enumerated(EnumType.STRING)
    // private UserRole role;

    /**
     * [추가] UserService의 changePassword에서 호출하는 메서드
     * (엔티티의 데이터를 변경할 때는 @Setter 대신 명확한 메서드 이름을 사용)
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}