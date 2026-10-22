package com.example.ddingsroom.suggest_post_image.dto;

import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "건의 첨부 이미지")
public class SuggestPostImageResponseDTO {
    @Schema(description = "이미지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "건의 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("suggest_post_id")
    private Long suggestPostId;
    @Schema(description = "저장된 파일 이름", example = "3f2b...-board.png", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("file_name")
    private String fileName;
    @Schema(description = "이미지 URL (S3)", example = "https://ddingsroom-bucket.s3.ap-northeast-2.amazonaws.com/suggest-images/3f2b...-board.png",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("file_url")
    private String fileUrl;
    @Schema(description = "MIME 타입", example = "image/png", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("file_type")
    private String fileType;
    @Schema(description = "업로드 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("uploaded_date")
    private LocalDateTime uploadedDate;

    public SuggestPostImageResponseDTO(SuggestPostImageEntity entity){
        this.id = entity.getId();
        this.suggestPostId = entity.getSuggestPost().getId();
        this.fileName = entity.getFileName();
        this.fileUrl = entity.getFileUrl();
        this.fileType = entity.getFileType();
        this.uploadedDate = entity.getUploadedDate();
    }
}
