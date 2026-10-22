package com.example.ddingsroom.community_post_comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커뮤니티 댓글 (대댓글 포함)")
public class CommunityPostCommentResponseDTO {

    @Schema(description = "댓글 ID", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    private Long id;

    @Schema(description = "게시글 ID", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("post_id")
    private Long postId;

    @Schema(description = "작성자 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "댓글 내용", example = "저도 그렇게 생각해요.", requiredMode = Schema.RequiredMode.REQUIRED,
            nullable = true)
    @JsonProperty("comment_content")
    private String commentContent;

    @Schema(description = "부모 댓글 ID (일반 댓글이면 null)", example = "8", requiredMode = Schema.RequiredMode.REQUIRED,
            nullable = true)
    @JsonProperty("parent_comment_id")
    private Long parentCommentId;

    @Schema(description = "작성 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2026-10-21T21:05:03", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "항상 \"성공\"", example = "성공", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("message")
    private String message;

    // 대댓글 목록
    @Schema(description = "대댓글 목록 (게시글별 댓글 조회에서만 채워지고 그 외에는 null)",
            requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    @JsonProperty("replies")
    private List<CommunityPostCommentResponseDTO> replies;

    // 댓글 타입 구분 (일반댓글/대댓글)
    @Schema(description = "댓글 종류", example = "COMMENT", allowableValues = {"COMMENT", "REPLY"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("comment_type")
    private String commentType; // "COMMENT" 또는 "REPLY"

    // 대댓글 개수
    @Schema(description = "대댓글 수 (대댓글 자신은 0)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("reply_count")
    private Long replyCount;
}
