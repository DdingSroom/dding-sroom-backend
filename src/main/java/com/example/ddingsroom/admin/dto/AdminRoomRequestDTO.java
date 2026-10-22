package com.example.ddingsroom.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminRoomRequestDTO {
    @Schema(description = "스터디룸 이름 (중복 불가)", example = "6", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roomName;
    @Schema(description = "스터디룸 상태 (생략 시 IDLE)", example = "IDLE", defaultValue = "IDLE",
            allowableValues = {"IDLE", "OCCUPIED", "MAINTENANCE"})
    private String roomStatus;
    
    public AdminRoomRequestDTO() {
        this.roomStatus = "IDLE"; // 기본값
    }
    
    public AdminRoomRequestDTO(String roomName, String roomStatus) {
        this.roomName = roomName;
        this.roomStatus = roomStatus != null ? roomStatus : "IDLE";
    }
} 