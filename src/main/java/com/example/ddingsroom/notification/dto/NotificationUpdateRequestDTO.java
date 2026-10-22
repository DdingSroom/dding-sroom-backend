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
public class NotificationUpdateRequestDTO {
    @Schema(description = "수정할 공지 ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long notificationId;
    @Schema(description = "공지 제목 (255자 이하)", example = "10월 스터디룸 운영 안내", maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    @Schema(description = "공지 내용", example = "10월 25일은 시설 점검으로 이용할 수 없습니다.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
