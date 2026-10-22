package com.example.ddingsroom.suggest_post.service;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestPostSeviceTest {

    @Mock
    private SuggestPostRepository suggestPostRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SuggestPostImageService suggestPostImageService;
    @InjectMocks
    private SuggestPostSevice suggestPostSevice;

    @Test
    void deleteCleansAttachedFilesThenRemovesPost() {
        SuggestPostEntity post = post(15L);
        when(suggestPostRepository.findById(10L)).thenReturn(Optional.of(post));

        suggestPostSevice.deleteSuggestPost(10L, 15L);

        InOrder order = inOrder(suggestPostImageService, suggestPostRepository);
        order.verify(suggestPostImageService).deleteFilesOfSuggestPost(10L);
        order.verify(suggestPostRepository).delete(post);
    }

    @Test
    void otherUserCannotDeleteAndFilesStay() {
        when(suggestPostRepository.findById(10L)).thenReturn(Optional.of(post(15L)));

        assertThatThrownBy(() -> suggestPostSevice.deleteSuggestPost(10L, 99L)).isInstanceOf(SecurityException.class);
        verifyNoInteractions(suggestPostImageService);
        verify(suggestPostRepository, never()).delete(any(SuggestPostEntity.class));
    }

    private SuggestPostEntity post(Long ownerId) {
        UserEntity owner = new UserEntity();
        owner.setId(ownerId);
        return new SuggestPostEntity(owner, "제목", "내용", 1, 1);
    }
}
