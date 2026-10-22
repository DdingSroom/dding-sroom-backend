package com.example.ddingsroom.community_post_comment.dto;

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
    private T data;

    public static <T> BaseResponseDTO<T> success(String message) {
        return BaseResponseDTO.<T>builder()
                .message(message)
                .build();
    }

    public static <T> BaseResponseDTO<T> success(String message, T data) {
        return BaseResponseDTO.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponseDTO<T> error(String error) {
        return BaseResponseDTO.<T>builder()
                .error(error)
                .build();
    }
}
