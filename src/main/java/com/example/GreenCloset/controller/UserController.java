package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*;
import com.example.GreenCloset.jwt.JwtUtil; // [수정] JwtUtil 임포트
import com.example.GreenCloset.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders; // [수정] HttpHeaders 임포트
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // [수정] @AuthenticationPrincipal 임포트
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil; // [수정] JwtUtil 주입

    /**
     * 회원 가입
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserResponseDto responseDto = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 로그인 (수정: 헤더에 토큰 반환)
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto requestDto) {
        // 1. UserService에서 인증 시도 (실패 시 예외 발생)
        // [수정] login 메서드는 인증된 User 객체를 반환하도록 수정 (이전 코드와 다름)
        User authenticatedUser = userService.loginAndGetUser(requestDto);

        // 2. 인증 성공 시 JWT 토큰 생성
        String token = jwtUtil.createToken(authenticatedUser.getEmail());

        // 3. Response Header에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // (Body 없이 Header에 토큰만 담아 200 OK 응답)
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // ★ 500 오류 해결을 위한 핵심 코드 ★
    // ★ 이 코드를 UserController 클래스 맨 아래에 추가하세요. ★
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        // Service에서 "비밀번호가 일치하지 않습니다" 등의 예외가 터지면
        // 500 오류 대신, 400 Bad Request와 함께 예외 메시지를 Body에 담아 보냅니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * 내 정보 조회 (수정: @AuthenticationPrincipal 사용)
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(
            @AuthenticationPrincipal User user // [수정] SecurityContext에서 인증된 User 객체 주입
    ) {
        // [수정] Long ID 대신 User 객체를 서비스로 전달
        UserInfoResponseDto responseDto = userService.getUserInfo(user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 비밀번호 변경 (수정: @AuthenticationPrincipal 사용)
     */
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto requestDto,
            @AuthenticationPrincipal User user // [수정] SecurityContext에서 인증된 User 객체 주입
    ) {
        // [수정] Long ID 대신 User 객체를 서비스로 전달
        userService.changePassword(requestDto, user);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}