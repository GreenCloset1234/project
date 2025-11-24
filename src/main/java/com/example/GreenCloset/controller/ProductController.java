package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*; // [수정]
import com.example.GreenCloset.service.ProductService;
import com.example.GreenCloset.service.S3Service;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@RequestMapping("/api/v1")
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
        String imageUrl = s3Service.uploadFile(image);
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
     * [v2 API] 특정 사용자가 작성한 상품 목록 조회 (프로필 보기)
     */
    @GetMapping("/products/users/{userId}/products")
    public ResponseEntity<List<ProductListResponseDto>> getProductsByUserId(
            @PathVariable Long userId
    ) {
        List<ProductListResponseDto> responseDtoList = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * [v2 API] 내가 작성한 상품 목록 조회 (마이페이지)
     */
    @GetMapping("/products/users/me/products")
    public ResponseEntity<List<ProductListResponseDto>> getMyProducts(
            @AuthenticationPrincipal User user
    ) {
        List<ProductListResponseDto> responseDtoList = productService.getProductsByUserId(user.getUserId());
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * [신규] 상품 거래 상태 변경 (예약 중 / 교환 가능)
     * (상품 등록자만 가능)
     */
    @PatchMapping("/products/{productId}/status")
    public ResponseEntity<Void> updateProductStatus(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStatusUpdateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        productService.updateProductStatus(productId, requestDto.getStatus(), user);
        return ResponseEntity.ok().build();
    }
}