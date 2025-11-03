package com.example.GreenCloset.controller;

import com.example.GreenCloset.dto.ProductCreateRequestDto;
import com.example.GreenCloset.dto.ProductDetailResponseDto;
import com.example.GreenCloset.dto.ProductListResponseDto;
import com.example.GreenCloset.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products") // 상품 공통 경로
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 (POST /products)
     */
    @PostMapping
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @Valid @RequestPart("dto") ProductCreateRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        // (TODO: 임시 ID. 추후 Spring Security에서 실제 유저 ID를 가져와야 함)
        Long currentUserId = 1L;

        ProductDetailResponseDto response = productService.createProduct(requestDto, imageFile, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 상품 목록 조회 (GET /products)
     */
    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getAllProducts() {
        List<ProductListResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 상품 상세 조회 (GET /products/{productId})
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductById(@PathVariable Long productId) {
        ProductDetailResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }
}