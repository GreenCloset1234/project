// global/exception/ErrorCode.java
package com.example.GreenCloset.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request (잘못된 요청)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 값이 올바르지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),

    // 401 Unauthorized (인증 실패)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),

    // 404 Not Found (찾을 수 없음)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),

    // 409 Conflict (충돌)
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    TRADE_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 거래가 완료된 상품입니다.");

    private final HttpStatus status;
    private final String message;
}