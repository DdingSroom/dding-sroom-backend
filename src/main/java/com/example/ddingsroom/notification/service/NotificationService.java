package com.example.ddingsroom.notification.service;

import com.example.ddingsroom.notification.dto.BaseResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationCreateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationReadResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationUpdateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationResponseDTO;
import com.example.ddingsroom.notification.entity.NotificationEntity;
import com.example.ddingsroom.notification.entity.NotificationReadEntity;
import com.example.ddingsroom.notification.repository.NotificationReadRepository;
import com.example.ddingsroom.notification.repository.NotificationRepository;
import com.example.ddingsroom.user.dto.CustomUserDetails;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationReadRepository notificationReadRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               NotificationReadRepository notificationReadRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationReadRepository = notificationReadRepository;
    }

    private <T> BaseResponseDTO<T> checkAdminPermission() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = userDetails.getUserEntity().getRole();

            if (!"ROLE_ADMIN".equals(role)) {
                return BaseResponseDTO.error("관리자 권한이 필요합니다.");
            }
            return null;
        } catch (Exception e) {
            return BaseResponseDTO.error("인증 정보를 확인할 수 없습니다.");
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserEntity().getId();
    }

    public BaseResponseDTO<NotificationResponseDTO> createNotification(NotificationCreateRequestDTO dto) {
        try {
            BaseResponseDTO<NotificationResponseDTO> permissionCheck = checkAdminPermission();
            if (permissionCheck != null) {
                return permissionCheck;
            }

            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                return BaseResponseDTO.error("제목은 필수 입력 항목입니다.");
            }

            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                return BaseResponseDTO.error("내용은 필수 입력 항목입니다.");
            }

            Long currentUserId = getCurrentUserId();
            UserEntity user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + currentUserId));

            NotificationEntity entity = new NotificationEntity();
            entity.setUser(user);
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());

            NotificationEntity savedEntity = notificationRepository.save(entity);
            NotificationResponseDTO responseDTO = convertToResponseDTO(savedEntity);

            return BaseResponseDTO.success("공지사항이 성공적으로 생성되었습니다!", responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("공지사항 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO<NotificationResponseDTO> updateNotification(NotificationUpdateRequestDTO dto) {
        try {
            BaseResponseDTO<NotificationResponseDTO> permissionCheck = checkAdminPermission();
            if (permissionCheck != null) {
                return permissionCheck;
            }

            if (dto.getNotificationId() == null) {
                return BaseResponseDTO.error("공지사항 ID는 필수입니다.");
            }

            Optional<NotificationEntity> optional = notificationRepository.findById(dto.getNotificationId());
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("공지사항을 찾을 수 없습니다.");
            }

            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                return BaseResponseDTO.error("제목은 필수 입력 항목입니다.");
            }

            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                return BaseResponseDTO.error("내용은 필수 입력 항목입니다.");
            }

            NotificationEntity entity = optional.get();
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());

            NotificationEntity updatedEntity = notificationRepository.save(entity);
            NotificationResponseDTO responseDTO = convertToResponseDTO(updatedEntity);

            return BaseResponseDTO.success("공지사항이 성공적으로 수정되었습니다!", responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("공지사항 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO<Void> deleteNotification(Long notificationId) {
        try {
            BaseResponseDTO<Void> permissionCheck = checkAdminPermission();
            if (permissionCheck != null) {
                return permissionCheck;
            }

            Optional<NotificationEntity> optional = notificationRepository.findById(notificationId);
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("삭제하려는 공지사항이 존재하지 않습니다.");
            }

            notificationRepository.deleteById(notificationId);
            return BaseResponseDTO.success("공지사항이 성공적으로 삭제되었습니다!");

        } catch (Exception e) {
            return BaseResponseDTO.error("공지사항 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO<List<NotificationResponseDTO>> getAllNotifications() {
        try {
            List<NotificationEntity> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
            Set<Long> readIds = findReadNotificationIds();
            List<NotificationResponseDTO> responseDTOs = notifications.stream()
                    .map(entity -> {
                        NotificationResponseDTO dto = convertToResponseDTO(entity);
                        dto.setIsRead(readIds.contains(entity.getId()));
                        return dto;
                    })
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("공지사항 목록 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("공지사항 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO<NotificationResponseDTO> getNotification(Long id) {
        try {
            Optional<NotificationEntity> optional = notificationRepository.findById(id);
            if (optional.isPresent()) {
                NotificationResponseDTO responseDTO = convertToResponseDTO(optional.get());
                responseDTO.setIsRead(isReadByCurrentUser(id));
                return BaseResponseDTO.success("공지사항 조회 성공", responseDTO);
            } else {
                return BaseResponseDTO.error("해당 공지사항을 찾을 수 없습니다. ID: " + id);
            }
        } catch (Exception e) {
            return BaseResponseDTO.error("공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public BaseResponseDTO<Void> incrementViewCount(Long notificationId) {
        try {
            Optional<NotificationEntity> optional = notificationRepository.findById(notificationId);
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("공지사항을 찾을 수 없습니다.");
            }

            NotificationEntity entity = optional.get();
            entity.setViewCount(entity.getViewCount() + 1);
            notificationRepository.save(entity);

            return BaseResponseDTO.success("조회수가 증가되었습니다!");

        } catch (Exception e) {
            return BaseResponseDTO.error("조회수 증가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO<Long> getRecentNotificationsCount() {
        try {
            LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
            Long count = notificationRepository.countNotificationsInLast3Days(threeDaysAgo);

            return BaseResponseDTO.success("최근 3일 공지사항 개수 조회 성공", count);
        } catch (Exception e) {
            return BaseResponseDTO.error("최근 공지사항 개수 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 공지 읽음 처리 (이미 읽었으면 기존 기록 그대로 반환)
    public NotificationReadResponseDTO markAsRead(Long notificationId) {
        Long userId = getCurrentUserIdOrNull();
        if (userId == null) {
            throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");
        }
        if (notificationId == null) {
            throw new IllegalArgumentException("공지사항 ID는 필수입니다.");
        }

        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        NotificationReadEntity read = notificationReadRepository.findByUser_IdAndNotification_Id(userId, notificationId)
                .orElseGet(() -> saveRead(userId, notification));

        return new NotificationReadResponseDTO(notificationId, true, read.getReadAt());
    }

    private NotificationReadEntity saveRead(Long userId, NotificationEntity notification) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        try {
            return notificationReadRepository.save(new NotificationReadEntity(user, notification));
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 이미 저장된 경우
            return notificationReadRepository.findByUser_IdAndNotification_Id(userId, notification.getId())
                    .orElseThrow(() -> e);
        }
    }

    private Set<Long> findReadNotificationIds() {
        Long userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(notificationReadRepository.findNotificationIdsByUserId(userId));
    }

    private boolean isReadByCurrentUser(Long notificationId) {
        Long userId = getCurrentUserIdOrNull();
        return userId != null && notificationReadRepository.existsByUser_IdAndNotification_Id(userId, notificationId);
    }

    // 비로그인 요청이면 null
    private Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserEntity().getId();
        }
        return null;
    }

    private NotificationResponseDTO convertToResponseDTO(NotificationEntity entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setViewCount(entity.getViewCount());
        dto.setMessage("성공");
        return dto;
    }

    // 사용자의 모든 공지사항 삭제 (JPA cascade로 자동 삭제됨)
    @Transactional
    public void deleteAllUserNotifications(Long userId) {
        // 이 메서드는 더 이상 필요하지 않습니다.
        // UserEntity 삭제 시 JPA cascade를 통해 자동으로 삭제됩니다.
    }
}