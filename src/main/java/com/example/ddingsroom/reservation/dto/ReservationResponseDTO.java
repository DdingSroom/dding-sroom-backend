package com.example.ddingsroom.reservation.dto;

import com.example.ddingsroom.reservation.entity.ReservationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "예약 목록 응답")
public class ReservationResponseDTO {
    @Schema(description = "처리 결과 메시지", example = "데이터 조회가 성공적으로 진행되었습니다.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
    @Schema(description = "예약 목록 (오류 시 빈 배열)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ReservationDTO> reservations;

    @Data
    @Schema(description = "예약 정보")
    public static class ReservationDTO {
        @Schema(description = "예약 ID", example = "26", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long id;
        @Schema(description = "예약한 사용자 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long userId;
        @Schema(description = "스터디룸 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long roomId;
        @Schema(description = "스터디룸 이름", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private String roomName;
        @Schema(description = "시작 일시 (KST)", example = "2026-10-21T21:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime startTime;
        @Schema(description = "종료 일시 (KST)", example = "2026-10-21T22:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime endTime;
        @Schema(description = "예약 상태", example = "RESERVED", allowableValues = {"RESERVED", "CANCELLED"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String status;
        @Schema(description = "예약 생성 일시", example = "2026-10-21T20:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createdAt;
        @Schema(description = "마지막 수정 일시 (취소 시각 포함)", example = "2026-10-21T20:05:03",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime updatedAt;

        public static ReservationDTO fromEntity(ReservationEntity entity) {
            ReservationDTO dto = new ReservationDTO();
            dto.setId(entity.getId());
            dto.setUserId(entity.getUser().getId());
            dto.setRoomId(entity.getRoom().getId());
            dto.setRoomName(entity.getRoom().getRoomName());
            dto.setStartTime(entity.getStartTime());
            dto.setEndTime(entity.getEndTime());
            dto.setStatus(entity.getStatus());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());
            return dto;
        }
    }
}
