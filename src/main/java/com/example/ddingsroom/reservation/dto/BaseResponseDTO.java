package com.example.ddingsroom.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReservationMessageResponse", description = "예약 API 처리 결과")
public class BaseResponseDTO {
    @Schema(description = "처리 결과 메시지 (성공·실패 모두 이 필드로 전달)", example = "예약이 성공적으로 완료되었습니다.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
