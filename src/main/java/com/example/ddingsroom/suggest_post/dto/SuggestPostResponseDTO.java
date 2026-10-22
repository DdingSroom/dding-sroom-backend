package com.example.ddingsroom.suggest_post.dto;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.util.SuggestPostCategory;
import com.example.ddingsroom.suggest_post.util.SuggestPostLocation;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "user_id",
        "suggest_title",
        "suggest_content",
        "created_at",
        "updated_at",
        "category",
        "location",
        "is_answered"
})
@Schema(description = "건의")
public class SuggestPostResponseDTO {
    @Schema(description = "건의 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "작성자 ID", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "건의 제목", example = "보드마카가 다 떨어졌어요", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("suggest_title")
    private String suggestTitle;

    @Schema(description = "건의 내용", example = "3번 방 보드마카를 채워주세요.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("suggest_content")
    private String suggestContent;

    @Schema(description = "작성 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "카테고리 이름", example = "기물 파손", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"미예약 사용자 신고", "분실물", "기물 파손", "시설 고장", "소음 공해", "기타"})
    private String category;
    @Schema(description = "위치 이름", example = "스터디룸3", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"스터디룸1", "스터디룸2", "스터디룸3", "스터디룸4", "스터디룸5"})
    private String location;

    @Schema(description = "관리자 답변 완료 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("is_answered")
    private boolean isAnswered;

    public SuggestPostResponseDTO(SuggestPostEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.suggestTitle = entity.getSuggestTitle();
        this.suggestContent = entity.getSuggestContent();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.category = SuggestPostCategory.fromValue(entity.getCategory()).getName();
        this.location = SuggestPostLocation.fromValue(entity.getLocation()).getName();
        this.isAnswered = entity.isAnswered();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSuggestTitle() {
        return suggestTitle;
    }

    public String getSuggestContent() {
        return suggestContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    @JsonProperty("is_answered")
    public boolean isAnswered() {
        return isAnswered;
    }
}
