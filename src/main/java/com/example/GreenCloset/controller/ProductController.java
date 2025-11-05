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
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    // (TODO: private final S3Service s3Service;)
    private final S3Service s3Service;

    /**
     * 1. 상품 등록 (인증 필요)
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @Valid @RequestPart("dto") ProductCreateRequestDto requestDto,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal User user // (판매자 정보)
    ) throws IOException {

        // (TODO: S3Service를 사용하여 이미지 업로드)
        // String imageUrl = s3Service.uploadFile(image);
        String s3Key = s3Service.uploadFile(image);
        //String tempImageUrl = "s3://temp-image-url.jpg"; // (임시 URL)

        //ProductDetailResponseDto responseDto = productService.createProduct(requestDto, user, tempImageUrl);
        ProductDetailResponseDto responseDto = productService.createProduct(requestDto, user, s3Key);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 2. 전체 상품 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getAllProducts() {
        List<ProductListResponseDto> responseDtoList = productService.getAllProducts();
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 3. 상품 상세 조회
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @PathVariable Long productId
    ) {
        // [수정] getProductById -> getProductDetail
        ProductDetailResponseDto responseDto = productService.getProductDetail(productId);
        return ResponseEntity.ok(responseDto);
    }
}