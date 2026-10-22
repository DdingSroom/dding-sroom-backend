package com.example.ddingsroom.notification.repository;

import com.example.ddingsroom.notification.entity.NotificationReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationReadEntity, Long> {

    Optional<NotificationReadEntity> findByUser_IdAndNotification_Id(Long userId, Long notificationId);

    boolean existsByUser_IdAndNotification_Id(Long userId, Long notificationId);

    @Query("SELECT r.notification.id FROM NotificationReadEntity r WHERE r.user.id = :userId")
    List<Long> findNotificationIdsByUserId(@Param("userId") Long userId);
}
