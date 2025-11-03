package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.Trade; // (Trade 엔티티 import)
import com.example.GreenCloset.domain.User; // (User 엔티티 import)
import com.example.GreenCloset.dto.*;
import com.example.GreenCloset.jwt.JwtUtil; // 1. JwtUtil import
import com.example.GreenCloset.service.ProductService;
import com.example.GreenCloset.service.TradeService;
import com.example.GreenCloset.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal; // 2. Spring Security의 Principal import
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final ProductService productService;
    private final TradeService tradeService;
    private final JwtUtil jwtUtil; // 3. JwtUtil 주입

    /**
     * 회원가입 (POST /users/signup)
     */
    @PostMapping("/users/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupRequestDto requestDto, HttpServletResponse response) {
        UserResponseDto userResponse = userService.signup(requestDto);

        // 4. (TODO 제거) 회원가입 성공 시 자동 로그인 토큰 발급
        String token = jwtUtil.createToken(requestDto.getEmail());
        jwtUtil.addTokenToCookie(token, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    /**
     * 로그인 (POST /auth/login)
     */
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        String token = userService.login(requestDto);

        // 4. (TODO 제거) 응답 헤더에 HttpOnly 쿠키로 토큰 추가
        jwtUtil.addTokenToCookie(token, response);

        return ResponseEntity.ok("로그인 성공");
    }

    /**
     * 로그아웃 (POST /auth/logout)
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // 4. (TODO 제거) 응답 헤더에 쿠키 만료
        jwtUtil.expireTokenCookie(response);

        return ResponseEntity.ok("로그아웃 성공");
    }

    /**
     * 내 정보 조회 (GET /users/me)
     */
    @GetMapping("/users/me")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(Principal principal) {
        // 5. (TODO 제거) Principal에서 인증된 사용자(email)를 찾아 User 객체 조회
        User user = userService.findUserByEmail(principal.getName());

        UserInfoResponseDto userInfo = userService.getUserInfo(user.getUserId());
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 내 정보 수정 (PATCH /users/me)
     */
    @PatchMapping("/users/me")
    public ResponseEntity<UserInfoResponseDto> updateUserInfo(
            Principal principal,
            @RequestPart("nickname") String nickname,
            @RequestPart("introduction") String introduction,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        // 5. (TODO 제거) Principal에서 사용자 조회
        User user = userService.findUserByEmail(principal.getName());

        // (TODO: ProductService처럼 S3 파일 업로드 로직을 구현해야 함)
        String imageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            // imageUrl = s3UploadService.upload(profileImage);
            System.out.println("S3 업로드 로직 필요: " + profileImage.getOriginalFilename());
        }

        UserInfoResponseDto updatedInfo = userService.updateUserInfo(
                user.getUserId(), nickname, introduction, imageUrl
        );

        return ResponseEntity.ok(updatedInfo);
    }

    /**
     * 비밀번호 변경 (PATCH /users/me/password)
     */
    @PatchMapping("/users/me/password")
    public ResponseEntity<String> changePassword(Principal principal, @Valid @RequestBody PasswordChangeRequestDto requestDto) {
        // 5. (TODO 제거) Principal에서 사용자 조회
        User user = userService.findUserByEmail(principal.getName());

        userService.changePassword(user.getUserId(), requestDto);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    /**
     * 내가 등록한 상품 조회 (GET /users/me/products)
     */
    @GetMapping("/users/me/products")
    public ResponseEntity<List<ProductListResponseDto>> getMyProducts(Principal principal) {
        // 5. (TODO 제거) Principal에서 사용자 조회
        User user = userService.findUserByEmail(principal.getName());

        List<ProductListResponseDto> myProducts = productService.getMyProducts(user.getUserId());
        return ResponseEntity.ok(myProducts);
    }

    /**
     * 나의 거래 내역 조회 (GET /users/me/trades)
     */
    @GetMapping("/users/me/trades")
    public ResponseEntity<List<Trade>> getMyTrades(Principal principal) {
        // 5. (TODO 제거) Principal에서 사용자 조회
        User user = userService.findUserByEmail(principal.getName());

        List<Trade> myTrades = tradeService.getMyTrades(user.getUserId());
        return ResponseEntity.ok(myTrades);
    }
}