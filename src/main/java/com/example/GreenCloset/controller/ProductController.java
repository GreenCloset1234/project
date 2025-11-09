package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ProductCreateRequestDto;
import com.example.GreenCloset.dto.ProductDetailResponseDto;
import com.example.GreenCloset.dto.ProductListResponseDto;
import com.example.GreenCloset.service.ProductService;
import com.example.GreenCloset.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1") // [수정] Base URL 변경 (신규 API 경로 통합)
public class ProductController {

    private final ProductService productService;
    private final S3Service s3Service;

    /**
     * 1. 상품 등록 (인증 필요)
     */
    @PostMapping(path = "/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @Valid @RequestPart("dto") ProductCreateRequestDto requestDto,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal User user
    ) throws IOException {

        // (참고: s3Service.uploadFile은 '완전한 URL'을 반환)
        String imageUrl = s3Service.uploadFile(image);

        // (ProductService는 '완전한 URL'을 받아 DB에 저장)
        ProductDetailResponseDto responseDto = productService.createProduct(requestDto, user, imageUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 2. 전체 상품 목록 조회
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductListResponseDto>> getAllProducts() {
        List<ProductListResponseDto> responseDtoList = productService.getAllProducts();
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 3. 상품 상세 조회
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @PathVariable Long productId
    ) {
        ProductDetailResponseDto responseDto = productService.getProductDetail(productId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * [신규] 특정 사용자가 작성한 상품 목록 조회 (인증 불필요)
     */
    @GetMapping("/users/{userId}/products")
    public ResponseEntity<List<ProductListResponseDto>> getProductsByUserId(
            @PathVariable Long userId
    ) {
        List<ProductListResponseDto> responseDtoList = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * [신규] 내가 작성한 상품 목록 조회 (인증 필요)
     */
    @GetMapping("/users/me/products")
    public ResponseEntity<List<ProductListResponseDto>> getMyProducts(
            @AuthenticationPrincipal User user
    ) {
        List<ProductListResponseDto> responseDtoList = productService.getProductsByUserId(user.getUserId());
        return ResponseEntity.ok(responseDtoList);
    }
}