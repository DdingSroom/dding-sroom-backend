package com.example.ddingsroom.community_post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공통 응답. 성공 시 message·data, 실패 시 error가 채워짐")
public class BaseResponseDTO<T> {
    @Schema(description = "성공 메시지 (실패 시 null)", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String message;
    @Schema(description = "실패 사유 (성공 시 null)", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String error;
    @Schema(description = "응답 데이터 (실패했거나 데이터가 없으면 null)", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private T data;  // 데이터 필드 추가!

    // 성공 응답 (메시지만)
    public static <T> BaseResponseDTO<T> success(String message) {
        return BaseResponseDTO.<T>builder()
                .message(message)
                .build();
    }

    // 성공 응답 (데이터 포함)
    public static <T> BaseResponseDTO<T> success(String message, T data) {
        return BaseResponseDTO.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    // 오류 응답
    public static <T> BaseResponseDTO<T> error(String error) {
        return BaseResponseDTO.<T>builder()
                .error(error)
                .build();
    }
}
