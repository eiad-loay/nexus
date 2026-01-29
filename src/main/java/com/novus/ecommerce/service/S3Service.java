package com.novus.ecommerce.service;

import com.novus.ecommerce.config.S3Config;
import com.novus.ecommerce.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    public PresignedUrlResponse getPresignedUploadUrl(Long productId, String fileName) {
        String extension = getExtension(fileName);
        String key = String.format("products/%d/%s%s", productId, UUID.randomUUID(), extension);

        log.info("Generating presigned upload URL for key: {}", key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(key)
                .contentType(getContentType(extension))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Config.getUploadExpirationMinutes()))
                .putObjectRequest(putObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

        log.debug("Generated presigned upload URL, expires in {} minutes",
                s3Config.getUploadExpirationMinutes());

        return new PresignedUrlResponse(presignedUrl, key, s3Config.getUploadExpirationMinutes());
    }

    public PresignedUrlResponse getPresignedGetUrl(String key) {

        if (key == null || key.isBlank()) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(key)
                .build();

        GetObjectPresignRequest getPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Config.getDownloadExpirationMinutes()))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(getPresignRequest).url().toString();

        return new PresignedUrlResponse(presignedUrl, key, s3Config.getDownloadExpirationMinutes());
    }

    public void deleteImage(String key) {

        if (key == null || key.isBlank()) {
            return;
        }

        log.info("Deleting file from S3: {}", key);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        log.info("File deleted successfully: {}", key);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File name must has extension.");
        }

        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private String getContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            default -> "image/jpeg";
        };
    }
}
