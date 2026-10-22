package com.example.ddingsroom.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 API 공통 응답")
public class AdminResponseDTO {
    @Schema(description = "처리 결과 메시지", example = "사용자 'kim'가 활성화되었습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
    @Schema(description = "성공 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean success;
    @Schema(description = "메시지 전용 응답이라 null (데이터가 담긴 응답은 API별 스키마 참고)", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private Object data;
    
    public AdminResponseDTO(String message) {
        this.message = message;
        this.success = true;
    }
    
    public AdminResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    public static AdminResponseDTO success(String message) {
        return new AdminResponseDTO(message, true);
    }
    
    public static AdminResponseDTO success(String message, Object data) {
        return new AdminResponseDTO(message, true, data);
    }
    
    public static AdminResponseDTO error(String message) {
        return new AdminResponseDTO(message, false);
    }
} 