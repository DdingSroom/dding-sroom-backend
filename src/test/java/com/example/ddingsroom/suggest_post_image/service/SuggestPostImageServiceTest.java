package com.example.ddingsroom.suggest_post_image.service;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import com.example.ddingsroom.suggest_post_image.repository.SuggestPostImageRepository;
import com.example.ddingsroom.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestPostImageServiceTest {

    private static final String URL = "https://ddingsroom-bucket.s3.ap-northeast-2.amazonaws.com/suggest-images/3f2b-%ED%95%9C%EA%B8%80.png";

    @Mock
    private SuggestPostImageRepository suggestPostImageRepository;
    @Mock
    private SuggestPostRepository suggestPostRepository;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private SuggestPostImageService suggestPostImageService;

    @Test
    void deleteImageRemovesRowAndDeletesStoredUrlAfterCommit() {
        SuggestPostImageEntity image = image(15L);
        when(suggestPostImageRepository.findById(3L)).thenReturn(Optional.of(image));

        suggestPostImageService.deleteImage(3L, 15L);

        verify(suggestPostImageRepository).delete(image);
        verify(s3Service).deleteFilesAfterCommit(List.of(URL));
    }

    @Test
    void deleteImageByOtherUserKeepsRowAndFile() {
        when(suggestPostImageRepository.findById(3L)).thenReturn(Optional.of(image(15L)));

        assertThatThrownBy(() -> suggestPostImageService.deleteImage(3L, 99L)).isInstanceOf(IllegalArgumentException.class);
        verify(suggestPostImageRepository, never()).delete(any());
        verifyNoInteractions(s3Service);
    }

    @Test
    void cleansFilesOfSuggestPostAndUser() {
        when(suggestPostImageRepository.findFileUrlsBySuggestPostId(10L)).thenReturn(List.of(URL));
        when(suggestPostImageRepository.findFileUrlsByUserId(15L)).thenReturn(List.of(URL, URL + "2"));

        suggestPostImageService.deleteFilesOfSuggestPost(10L);
        suggestPostImageService.deleteFilesOfUser(15L);

        verify(s3Service).deleteFilesAfterCommit(List.of(URL));
        verify(s3Service).deleteFilesAfterCommit(List.of(URL, URL + "2"));
    }

    private SuggestPostImageEntity image(Long ownerId) {
        UserEntity owner = new UserEntity();
        owner.setId(ownerId);
        SuggestPostEntity post = new SuggestPostEntity(owner, "제목", "내용", 1, 1);
        return new SuggestPostImageEntity(post, "3f2b-%ED%95%9C%EA%B8%80.png", URL, "image/png", LocalDateTime.now());
    }
}
