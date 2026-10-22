package com.example.ddingsroom.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원·인증 API 공통 응답")
public class ResponseDTO {
    @Schema(description = "처리 결과 메시지 (성공·실패 모두 이 필드로 전달)", example = "회원가입이 성공적으로 완료되었습니다.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    public ResponseDTO() {}

    public ResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
