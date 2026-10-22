package com.example.ddingsroom.suggest_post_image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final String S3_DIRECTORY_NAME = "suggest-images/";

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFilename;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, S3_DIRECTORY_NAME + uniqueFileName, inputStream, objectMetadata));
                    //.withCannedAcl(CannedAccessControlList.PublicRead));
        }

        return amazonS3Client.getUrl(bucketName, S3_DIRECTORY_NAME + uniqueFileName).toString();
    }

    public void deleteFile(String fileUrl) {
        amazonS3Client.deleteObject(bucketName, toKey(fileUrl));
    }

    // DB 삭제가 커밋된 뒤 S3 파일 삭제. 실패해도 요청은 성공 처리하고 로그만 남김
    public void deleteFilesAfterCommit(List<String> fileUrls) {
        if (fileUrls.isEmpty()) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteQuietly(fileUrls);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteQuietly(fileUrls);
            }
        });
    }

    private void deleteQuietly(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            try {
                deleteFile(fileUrl);
            } catch (Exception e) {
                log.warn("S3 파일 삭제 실패: {}", fileUrl, e);
            }
        }
    }

    // 저장된 URL·파일명은 URL 인코딩된 상태라 디코딩해야 실제 키와 일치
    static String toKey(String fileUrl) {
        int start = fileUrl.indexOf(S3_DIRECTORY_NAME);
        String encodedName = start < 0 ? fileUrl : fileUrl.substring(start + S3_DIRECTORY_NAME.length());
        return S3_DIRECTORY_NAME + URLDecoder.decode(encodedName.replace("+", "%2B"), StandardCharsets.UTF_8);
    }
}