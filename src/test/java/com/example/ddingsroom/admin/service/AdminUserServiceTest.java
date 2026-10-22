package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.entity.VerificationCode;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    private static final String EMAIL = "kim@mju.ac.kr";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshRepository refreshRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private SuggestPostImageService suggestPostImageService;
    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void deleteUserRevokesSessionAndCleansFilesBeforeDeleting() {
        UserEntity user = user(EMAIL, "ROLE_USER", "normal");
        VerificationCode code = new VerificationCode("123456", EMAIL);
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(verificationCodeRepository.findByEmail(EMAIL)).thenReturn(Optional.of(code));

        ResponseEntity<AdminResponseDTO> response = adminUserService.deleteUser(20L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        InOrder order = inOrder(refreshRepository, verificationCodeRepository, suggestPostImageService, userRepository);
        order.verify(refreshRepository).deleteByUsername(EMAIL);
        order.verify(verificationCodeRepository).delete(code);
        order.verify(suggestPostImageService).deleteFilesOfUser(20L);
        order.verify(userRepository).delete(user);
    }

    // 이메일 없는 계정은 null 조건으로 다른 행을 지우지 않도록 토큰 정리를 건너뜀
    @Test
    void deleteUserWithoutEmailSkipsTokenCleanup() {
        UserEntity user = user(null, "ROLE_ADMIN", "normal");
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));

        assertThat(adminUserService.deleteUser(20L).getStatusCode().value()).isEqualTo(200);
        verifyNoInteractions(refreshRepository, verificationCodeRepository);
        verify(userRepository).delete(user);
    }

    @Test
    void roleChangeRevokesRefreshTokens() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(user(EMAIL, "ROLE_ADMIN", "normal")));

        assertThat(adminUserService.updateUserRole(20L, "ROLE_USER").getStatusCode().value()).isEqualTo(200);
        verify(refreshRepository).deleteByUsername(EMAIL);
    }

    @Test
    void sameRoleKeepsSession() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(user(EMAIL, "ROLE_USER", "normal")));

        assertThat(adminUserService.updateUserRole(20L, "ROLE_USER").getStatusCode().value()).isEqualTo(200);
        verify(refreshRepository, never()).deleteByUsername(any());
    }

    @Test
    void blockingRevokesRefreshTokens() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(user(EMAIL, "ROLE_USER", "normal")));

        assertThat(adminUserService.updateUserStatus(20L, "blocked").getStatusCode().value()).isEqualTo(200);
        verify(refreshRepository).deleteByUsername(EMAIL);
    }

    @Test
    void unblockingKeepsSession() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(user(EMAIL, "ROLE_USER", "blocked")));

        assertThat(adminUserService.updateUserStatus(20L, "normal").getStatusCode().value()).isEqualTo(200);
        verify(refreshRepository, never()).deleteByUsername(any());
    }

    private UserEntity user(String email, String role, String state) {
        UserEntity user = new UserEntity();
        user.setId(20L);
        user.setEmail(email);
        user.setUsername("kim");
        user.setRole(role);
        user.setState(state);
        return user;
    }
}
