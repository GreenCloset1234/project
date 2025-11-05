package com.example.GreenCloset.service;

// 1. [수정] import 구문이 완전히 변경되었습니다. (AWS SDK v2/v3용)
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    // 1. application.properties에서 S3 설정을 자동으로 주입받음
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 파일을 업로드하고, 저장된 파일의 키(Key)를 반환합니다.
     * (전체 URL이 아닌, 파일 키를 반환해야 ProductService 로직과 맞습니다)
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {

        // 2. 파일 원본 이름에서 확장자 추출
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        // 3. 중복 방지를 위한 UUID 파일 이름 생성 (경로 포함)
        String fileKey = "products/" + UUID.randomUUID().toString() + fileExtension;

        // 4. [수정] 업로드 방식 변경 (PutObjectRequest 객체 사용)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();

        // 5. [수정] S3에 파일 업로드 (InputStream과 RequestBody 사용)
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

        // 6. ProductService에서 사용할 파일 키(Key) 반환
        return fileKey;
    }

    /**
     * 파일 이름에서 확장자를 추출하는 헬퍼 메서드
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            // (혹시 모를 예외 처리: 확장자가 없으면 .jpg 강제)
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}