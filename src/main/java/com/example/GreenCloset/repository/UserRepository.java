package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 엔티티에 접근하기 위한 Spring Data JPA 리포지토리
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일(로그인 ID)로 사용자를 찾는 메서드
     * (1) 회원가입 시 중복 체크
     * (2) 로그인 시 사용자 조회
     * (3) JWT 토큰 검증 시 사용자 조회
     * @param email (사용자 이메일)
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    // (필요에 따라 findByNickname 등 다른 메서드 추가 가능)
    // Optional<User> findByNickname(String nickname);
}