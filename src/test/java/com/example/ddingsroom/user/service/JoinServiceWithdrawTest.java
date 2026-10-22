package com.example.ddingsroom.user.service;

import com.example.ddingsroom.community_post.service.CommunityPostService;
import com.example.ddingsroom.community_post_comment.service.CommunityPostCommentService;
import com.example.ddingsroom.notification.service.NotificationService;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.suggest_post.service.SuggestPostSevice;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import com.example.ddingsroom.user.dto.ResponseDTO;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.RefreshRepository;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.user.repository.VerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinServiceWithdrawTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private RefreshRepository refreshRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CommunityPostService communityPostService;
    @Mock
    private CommunityPostCommentService communityPostCommentService;
    @Mock
    private SuggestPostSevice suggestPostSevice;
    @Mock
    private NotificationService notificationService;
    @Mock
    private SuggestPostImageService suggestPostImageService;
    @InjectMocks
    private JoinService joinService;

    @Test
    void withdrawCleansTokensAndFilesBeforeDeletingUser() {
        UserEntity user = new UserEntity();
        user.setId(20L);
        user.setEmail("kim@mju.ac.kr");
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(verificationCodeRepository.findByEmail("kim@mju.ac.kr")).thenReturn(Optional.empty());

        ResponseEntity<ResponseDTO> response = joinService.withdrawUserById(20L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        InOrder order = inOrder(refreshRepository, suggestPostImageService, userRepository);
        order.verify(refreshRepository).deleteByUsername("kim@mju.ac.kr");
        order.verify(suggestPostImageService).deleteFilesOfUser(20L);
        order.verify(userRepository).delete(user);
    }
}
