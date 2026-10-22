package com.example.ddingsroom.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReservationCancelRequestDTO {
    // userId는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.
    @Schema(description = "취소할 예약 ID", example = "26", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long reservationId;
}
