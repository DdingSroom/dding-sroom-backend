package com.example.ddingsroom.suggest_post_comment.dto;

import com.example.ddingsroom.suggest_post_comment.entity.SuggestPostCommentEntity;
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
@Schema(description = "건의에 대한 관리자 답변")
public class SuggestPostCommentResponseDTO {
    @Schema(description = "답변 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "건의 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("suggest_post_id")
    private Long suggestPostId;

    @Schema(description = "답변한 관리자 ID", example = "14", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "답변 내용", example = "채워두었습니다. 감사합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("answer_content")
    private String answerContent;

    @Schema(description = "작성 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public SuggestPostCommentResponseDTO(SuggestPostCommentEntity entity) {
        this.id = entity.getId();
        this.suggestPostId = entity.getSuggestPost().getId();
        this.userId = entity.getUserId();
        this.answerContent = entity.getAnswerContent();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

}
