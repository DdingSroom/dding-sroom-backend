package com.example.ddingsroom.community_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커뮤니티 게시글")
public class CommunityPostResponseDTO {

    @Schema(description = "게시글 ID", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    private Long id;

    @Schema(description = "작성자 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "제목", example = "스터디룸 이용 후기", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    @JsonProperty("title")
    private String title;

    @Schema(description = "본문", example = "조용하고 좋았어요.", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    @JsonProperty("content")
    private String content;

    @Schema(description = "게시판 분류 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    @JsonProperty("category")
    private Integer category;

    @Schema(description = "작성 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시 (수정 전에는 작성 일시와 같음)", example = "2026-10-21T21:05:03",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "항상 \"성공\"", example = "성공", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("message")
    private String message;
}
