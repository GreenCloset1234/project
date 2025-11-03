// global/GlobalExceptionHandler.java
package com.example.GreenCloset.global;

import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorResponse; // (아래 5번에서 만들 DTO)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice // 1. 모든 @RestController에서 발생하는 예외를 가로챔
public class GlobalExceptionHandler {

    // 2. 우리가 만든 CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse response = new ErrorResponse(e.getErrorCode().getStatus().value(), e.getMessage());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    // 3. @Valid 유효성 검사 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        // (가장 첫 번째 에러 메시지를 가져옴)
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse response = new ErrorResponse(400, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 4. (선택) 그 외 모든 예외 처리 (500 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = new ErrorResponse(500, "서버 내부 오류가 발생했습니다.");
        // (서버 로그에 실제 오류 출력)
        e.printStackTrace();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}