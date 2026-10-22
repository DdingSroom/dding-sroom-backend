package com.example.ddingsroom.suggest_post_image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class S3ServiceTest {

    private static final String BUCKET = "test-bucket";

    private final AmazonS3Client amazonS3Client = mock(AmazonS3Client.class);
    private final S3Service s3Service = new S3Service(amazonS3Client);

    @BeforeEach
    void setBucket() {
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET);
    }

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    // 업로드 때 SDK가 만든 URL과 거기서 잘라 저장한 file_name 둘 다 원래 키로 복원되는지
    @ParameterizedTest
    @ValueSource(strings = {"3f2b-board.png", "3f2b-한글 사진.png", "3f2b-a+b (1)%.png"})
    void storedUrlAndFileNameResolveToUploadedKey(String uploadedName) {
        String key = "suggest-images/" + uploadedName;
        AmazonS3 sdk = AmazonS3ClientBuilder.standard()
                .withRegion("ap-northeast-2")
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("id", "secret")))
                .build();
        String fileUrl = sdk.getUrl("ddingsroom-bucket", key).toString();
        String storedFileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

        assertThat(S3Service.toKey(fileUrl)).isEqualTo(key);
        assertThat(S3Service.toKey(storedFileName)).isEqualTo(key);
    }

    @Test
    void deleteFileUsesDecodedKey() {
        s3Service.deleteFile("https://test-bucket.s3.ap-northeast-2.amazonaws.com/suggest-images/3f2b-%ED%95%9C%EA%B8%80.png");

        verify(amazonS3Client).deleteObject(BUCKET, "suggest-images/3f2b-한글.png");
    }

    @Test
    void deletesOnlyAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();
        s3Service.deleteFilesAfterCommit(List.of("https://x/suggest-images/a.png"));
        verifyNoInteractions(amazonS3Client);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(amazonS3Client).deleteObject(BUCKET, "suggest-images/a.png");
    }

    @Test
    void rollbackKeepsFiles() {
        TransactionSynchronizationManager.initSynchronization();
        s3Service.deleteFilesAfterCommit(List.of("https://x/suggest-images/a.png"));

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(sync -> sync.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
        verifyNoInteractions(amazonS3Client);
    }

    @Test
    void failureIsLoggedAndOtherFilesAreStillDeleted() {
        doThrow(new AmazonServiceException("denied")).when(amazonS3Client).deleteObject(BUCKET, "suggest-images/a.png");

        s3Service.deleteFilesAfterCommit(List.of("https://x/suggest-images/a.png", "https://x/suggest-images/b.png"));

        verify(amazonS3Client).deleteObject(BUCKET, "suggest-images/b.png");
    }
}
