package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ProductCreateRequestDto;
import com.example.GreenCloset.dto.ProductDetailResponseDto;
import com.example.GreenCloset.dto.ProductListResponseDto;
import com.example.GreenCloset.repository.ProductRepository;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 기본적으로 트랜잭션 안에서 동작
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // (TODO: S3Service 또는 FileStorageService를 만들고 주입받아야 합니다)
    // private final S3UploadService s3UploadService;

    /**
     * 상품 등록 (POST /products)
     */
    public ProductDetailResponseDto createProduct(ProductCreateRequestDto requestDto, MultipartFile imageFile, Long userId) {

        // 1. 상품을 등록할 유저(판매자)를 조회
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 ID를 찾을 수 없습니다: " + userId));

        String imageUrl = null;

        // 2. (TODO) 이미지 파일 업로드 로직
        if (imageFile != null && !imageFile.isEmpty()) {
            // (이 부분은 S3Service 등 파일 업로드 전용 서비스를 만들어야 합니다)
            // imageUrl = s3UploadService.uploadFile(imageFile);

            // (임시 코드 - 실제로는 S3 URL이 들어가야 함)
            imageUrl = "/uploads/" + imageFile.getOriginalFilename();
            System.out.println("파일 업로드 처리 필요: " + imageUrl);
        }

        // 3. Product 엔티티 생성
        Product newProduct = Product.builder()
                .user(seller)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .productImageUrl(imageUrl) // 2번에서 얻은 URL 저장
                .build();

        // 4. DB에 저장
        Product savedProduct = productRepository.save(newProduct);

        // 5. DTO로 변환하여 반환
        return new ProductDetailResponseDto(savedProduct);
    }

    /**
     * 상품 목록 조회 (GET /products)
     */
    @Transactional(readOnly = true)
    public List<ProductListResponseDto> getAllProducts() {

        // (성능 최적화: N+1 문제가 발생할 수 있습니다.
        //  추후 ProductRepository에서 JPQL로 DTO를 바로 조회하는 것을 권장합니다.)

        return productRepository.findAll().stream()
                .map(product -> new ProductListResponseDto(
                        product.getProductId(),
                        product.getTitle(),
                        product.getProductImageUrl(), // ERD상 썸네일 = 이미지URL
                        product.getUser().getNickname() // N+1 유발 지점
                ))
                .collect(Collectors.toList());
    }

    /**
     * 상품 상세 조회 (GET /products/{productId})
     */
    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        // DTO 생성자에 엔티티를 넘겨서 변환
        return new ProductDetailResponseDto(product);
    }

    /**
     * 내가 등록한 상품 조회 (GET /users/me/products)
     */
    @Transactional(readOnly = true)
    public List<ProductListResponseDto> getMyProducts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // (이 기능을 위해 Repository 수정이 필요합니다 -> 아래 1번 참고)
        List<Product> myProducts = productRepository.findAllByUser(user);

        return myProducts.stream()
                .map(product -> new ProductListResponseDto(
                        product.getProductId(),
                        product.getTitle(),
                        product.getProductImageUrl(),
                        user.getNickname() // 이미 유저 정보가 있으므로 N+1 없음
                ))
                .collect(Collectors.toList());
    }
}