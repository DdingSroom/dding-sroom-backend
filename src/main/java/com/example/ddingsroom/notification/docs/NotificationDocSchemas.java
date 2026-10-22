package com.example.ddingsroom.notification.docs;

import io.swagger.v3.oas.annotations.media.Schema;

// 문서용 스키마 (실제 응답 객체로는 쓰지 않음)
public final class NotificationDocSchemas {

    private NotificationDocSchemas() {
    }

    @Schema(name = "NotificationErrorResponse", description = "공지 읽음 처리 실패 응답")
    public static class ErrorResponse {
        @Schema(description = "항상 null", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        public String message;
        @Schema(description = "실패 사유", example = "공지사항을 찾을 수 없습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        public String error;
        @Schema(description = "항상 null", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        public Object data;
    }
}
