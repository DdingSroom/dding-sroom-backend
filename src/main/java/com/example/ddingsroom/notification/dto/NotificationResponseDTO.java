package com.example.ddingsroom.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항")
public class NotificationResponseDTO {
    @Schema(description = "공지 ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "작성한 관리자 ID", example = "14", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    @Schema(description = "제목", example = "10월 스터디룸 운영 안내", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    @Schema(description = "내용", example = "10월 25일은 시설 점검으로 이용할 수 없습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
    @Schema(description = "작성 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;
    @Schema(description = "수정 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updatedAt;
    @Schema(description = "조회수", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer viewCount;
    @Schema(description = "항상 \"성공\"", example = "성공", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
    @Schema(description = "로그인한 사용자의 읽음 여부. 목록·상세 조회에만 포함되며 비로그인 요청은 항상 false",
            example = "false")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("isRead")
    private Boolean isRead;
}
