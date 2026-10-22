package com.example.ddingsroom.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지 읽음 처리 결과")
public class NotificationReadResponseDTO {
    @Schema(description = "공지 ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long notificationId;
    @Schema(description = "항상 true", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("isRead")
    private Boolean isRead;
    @Schema(description = "처음 읽은 일시 (다시 호출해도 바뀌지 않음)", example = "2026-10-22T14:03:11",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime readAt;
}
