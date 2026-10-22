package com.example.ddingsroom.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    // userId는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.
    @Schema(description = "스터디룸 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roomId;
    @Schema(description = "시작 일시 (KST)", example = "2026-10-21T21:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime reservationStartTime;
    @Schema(description = "종료 일시 (KST, 시작 후 1시간 또는 2시간)", example = "2026-10-21T22:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime reservationEndTime;
}
