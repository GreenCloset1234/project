package com.example.GreenCloset.domain;

import jakarta.persistence.Column; // [수정] 이 import 구문이 누락되었습니다.
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // (이 클래스를 상속받는 엔티티에게 필드만 물려줍니다)
@EntityListeners(AuditingEntityListener.class) // (자동으로 시간을 감지합니다)
public abstract class BaseEntity {

    @CreatedDate // (엔티티 생성 시 시간 자동 저장)
    @Column(updatable = false) // [수정] 이제 이 부분이 정상 작동합니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // (엔티티 수정 시 시간 자동 저장)
    private LocalDateTime updatedAt;
}