package com.example.ddingsroom.notification.service;

import com.example.ddingsroom.notification.dto.NotificationReadResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationResponseDTO;
import com.example.ddingsroom.notification.entity.NotificationEntity;
import com.example.ddingsroom.notification.entity.NotificationReadEntity;
import com.example.ddingsroom.notification.repository.NotificationReadRepository;
import com.example.ddingsroom.notification.repository.NotificationRepository;
import com.example.ddingsroom.user.dto.CustomUserDetails;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationReadServiceTest {

    private static final LocalDateTime READ_AT = LocalDateTime.of(2026, 10, 22, 14, 3, 11);

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationReadRepository notificationReadRepository;
    @InjectMocks
    private NotificationService notificationService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void markAsReadSavesFirstRead() {
        login(10L);
        when(notificationRepository.findById(3L)).thenReturn(Optional.of(notification(3L)));
        when(notificationReadRepository.findByUser_IdAndNotification_Id(10L, 3L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(user(10L)));
        when(notificationReadRepository.save(any(NotificationReadEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationReadResponseDTO result = notificationService.markAsRead(3L);

        assertThat(result.getNotificationId()).isEqualTo(3L);
        assertThat(result.getIsRead()).isTrue();
        assertThat(result.getReadAt()).isNotNull();
        verify(notificationReadRepository).save(any(NotificationReadEntity.class));
    }

    @Test
    void markAsReadTwiceKeepsFirstRecord() {
        login(10L);
        when(notificationRepository.findById(3L)).thenReturn(Optional.of(notification(3L)));
        when(notificationReadRepository.findByUser_IdAndNotification_Id(10L, 3L)).thenReturn(Optional.of(read()));

        NotificationReadResponseDTO result = notificationService.markAsRead(3L);

        assertThat(result.getReadAt()).isEqualTo(READ_AT);
        verify(notificationReadRepository, never()).save(any());
    }

    @Test
    void markAsReadHandlesConcurrentInsert() {
        login(10L);
        when(notificationRepository.findById(3L)).thenReturn(Optional.of(notification(3L)));
        when(notificationReadRepository.findByUser_IdAndNotification_Id(10L, 3L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(read()));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user(10L)));
        when(notificationReadRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThat(notificationService.markAsRead(3L).getReadAt()).isEqualTo(READ_AT);
    }

    @Test
    void markAsReadRejectsInvalidRequests() {
        assertThatThrownBy(() -> notificationService.markAsRead(3L))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);

        login(10L);
        assertThatThrownBy(() -> notificationService.markAsRead(null))
                .isInstanceOf(IllegalArgumentException.class);

        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> notificationService.markAsRead(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listMarksReadStateForLoggedInUser() {
        login(10L);
        when(notificationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(notification(3L), notification(2L)));
        when(notificationReadRepository.findNotificationIdsByUserId(10L)).thenReturn(List.of(3L));

        List<NotificationResponseDTO> data = listData();

        assertThat(data).extracting(NotificationResponseDTO::getId, NotificationResponseDTO::getIsRead)
                .containsExactly(tuple(3L, true), tuple(2L, false));
    }

    @Test
    void listForAnonymousIsUnread() {
        when(notificationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(notification(3L)));

        assertThat(listData()).extracting(NotificationResponseDTO::getIsRead).containsExactly(false);
        verifyNoInteractions(notificationReadRepository);
    }

    @Test
    void detailShowsReadState() {
        login(10L);
        when(notificationRepository.findById(3L)).thenReturn(Optional.of(notification(3L)));
        when(notificationReadRepository.existsByUser_IdAndNotification_Id(10L, 3L)).thenReturn(true);

        NotificationResponseDTO dto = (NotificationResponseDTO) notificationService.getNotification(3L).getData();

        assertThat(dto.getIsRead()).isTrue();
    }

    @SuppressWarnings("unchecked")
    private List<NotificationResponseDTO> listData() {
        return (List<NotificationResponseDTO>) notificationService.getAllNotifications().getData();
    }

    private void login(Long userId) {
        UserEntity user = user(userId);
        user.setRole("ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), null, List.of()));
    }

    private static UserEntity user(Long id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }

    private static NotificationEntity notification(Long id) {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(id);
        notification.setTitle("제목");
        notification.setContent("내용");
        notification.setViewCount(0);
        return notification;
    }

    private static NotificationReadEntity read() {
        NotificationReadEntity read = new NotificationReadEntity();
        ReflectionTestUtils.setField(read, "readAt", READ_AT);
        return read;
    }
}
