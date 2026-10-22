package com.example.ddingsroom.notification.entity;

import com.example.ddingsroom.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.time.ZoneId;

// 사용자별 공지 읽음 기록 (공지·회원 삭제 시 DB에서 함께 삭제)
@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification_read", uniqueConstraints = @UniqueConstraint(
        name = "uk_notification_read_user_notification", columnNames = {"user_id", "notification_id"}))
public class NotificationReadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_read_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_read_notification"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private NotificationEntity notification;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    public NotificationReadEntity(UserEntity user, NotificationEntity notification) {
        this.user = user;
        this.notification = notification;
        this.readAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
