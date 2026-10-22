package com.example.ddingsroom.notification.controller;

import com.example.ddingsroom.notification.docs.NotificationApiDocs;
import com.example.ddingsroom.notification.dto.BaseResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationCreateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationReadRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationReadResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationUpdateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationViewCountRequestDTO;
import com.example.ddingsroom.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController implements NotificationApiDocs {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<NotificationResponseDTO>> createNotification(@RequestBody NotificationCreateRequestDTO dto) {
        BaseResponseDTO<NotificationResponseDTO> response = notificationService.createNotification(dto);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO<NotificationResponseDTO>> updateNotification(@RequestBody NotificationUpdateRequestDTO dto) {
        BaseResponseDTO<NotificationResponseDTO> response = notificationService.updateNotification(dto);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteNotification(@PathVariable Long id) {
        BaseResponseDTO<Void> response = notificationService.deleteNotification(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<BaseResponseDTO<List<NotificationResponseDTO>>> getAllNotifications() {
        BaseResponseDTO<List<NotificationResponseDTO>> response = notificationService.getAllNotifications();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<NotificationResponseDTO>> getNotification(@PathVariable Long id) {
        BaseResponseDTO<NotificationResponseDTO> response = notificationService.getNotification(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/view")
    public ResponseEntity<BaseResponseDTO<Void>> incrementViewCount(@RequestBody NotificationViewCountRequestDTO dto) {
        BaseResponseDTO<Void> response = notificationService.incrementViewCount(dto.getNotificationId());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/recent-count")
    public ResponseEntity<BaseResponseDTO<Long>> getRecentNotificationsCount() {
        BaseResponseDTO<Long> response = notificationService.getRecentNotificationsCount();
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/read")
    public ResponseEntity<BaseResponseDTO<NotificationReadResponseDTO>> markAsRead(@RequestBody NotificationReadRequestDTO dto) {
        try {
            NotificationReadResponseDTO result = notificationService.markAsRead(dto.getNotificationId());
            return ResponseEntity.ok(BaseResponseDTO.success("공지사항을 읽음 처리했습니다.", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponseDTO.error(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDTO.error(e.getMessage()));
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDTO.error("공지사항 읽음 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
