package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ProductCreateRequestDto;
import com.example.GreenCloset.dto.ProductDetailResponseDto;
import com.example.GreenCloset.dto.ProductListResponseDto;
// [수정] import 구문 추가
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    // (TODO: private final S3Service s3Service; // 이미지 업로드 서비스)

    @Transactional
    public ProductDetailResponseDto createProduct(ProductCreateRequestDto requestDto, User user, String imageUrl) {
        Product newProduct = Product.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .productImageUrl(imageUrl)
                .user(user)
                .build();

        Product savedProduct = productRepository.save(newProduct);
        return ProductDetailResponseDto.fromEntity(savedProduct);
    }

    public List<ProductListResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                // [수정] import가 추가되어 정상 작동
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailResponseDto.fromEntity(product);
    }
}