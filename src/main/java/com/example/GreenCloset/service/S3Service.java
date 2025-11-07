package com.example.GreenCloset.service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.base-url}")
    private String s3BaseUrl;

    /**
     * S3에 [상품] 파일을 업로드하고, 저장된 '완전한 URL'을 반환합니다.
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileKey = "products/" + createUniqueFileKey(multipartFile.getOriginalFilename());
        uploadToS3(multipartFile, fileKey);
        return getFullFileUrl(fileKey);
    }

    /**
     * S3에 [프로필] 파일을 업로드하고, 저장된 '완전한 URL'을 반환합니다.
     */
    public String uploadProfileImage(MultipartFile multipartFile) throws IOException {
        String fileKey = "profiles/" + createUniqueFileKey(multipartFile.getOriginalFilename());
        uploadToS3(multipartFile, fileKey);
        return getFullFileUrl(fileKey);
    }

    /**
     * S3에서 파일을 삭제합니다. (완전한 URL을 받아서 키를 추출)
     */
    public void deleteFile(String fullFileUrl) {
        if (fullFileUrl == null || fullFileUrl.isEmpty() || !fullFileUrl.startsWith(s3BaseUrl)) {
            return;
        }
        String fileKey = fullFileUrl.substring(s3BaseUrl.length());
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * [수정] 'private' -> 'public'으로 변경
     * 파일 '키(Key)'를 '완전한 HTTPS URL'로 변환합니다. (UserService가 호출)
     */
    public String getFullFileUrl(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return null;
        }
        return s3BaseUrl + fileKey;
    }


    // --- 헬퍼 메서드 (이하 동일) ---

    private void uploadToS3(MultipartFile multipartFile, String fileKey) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
    }

    private String createUniqueFileKey(String originalFilename) {
        String fileExtension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + fileExtension;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}