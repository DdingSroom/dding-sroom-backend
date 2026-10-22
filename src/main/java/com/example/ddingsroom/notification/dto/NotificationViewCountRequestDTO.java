package com.example.ddingsroom.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationViewCountRequestDTO {
    @Schema(description = "조회수를 올릴 공지 ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long notificationId;
}
