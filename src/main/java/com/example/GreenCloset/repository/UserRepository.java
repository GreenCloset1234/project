package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티에 접근하기 위한 Spring Data JPA 리포지토리
 */
// [!!] @Repository 어노테이션이 빠져있습니다. 추가하는 것을 권장합니다.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일(로그인 ID)로 사용자를 찾는 메서드
     * ... (주석) ...
     * @param email (사용자 이메일)
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * [수정] ChatService에서 닉네임으로 사용자를 찾기 위해 주석 해제
     */
    Optional<User> findByNickname(String nickname);
}