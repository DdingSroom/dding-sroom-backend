package com.example.ddingsroom.community_post_comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 작성·수정·삭제 요청 (API마다 필요한 필드가 다름)")
public class CommunityPostCommentRequestDTO {

    @Schema(description = "댓글 ID (수정·삭제 시 필수)", example = "8")
    @JsonProperty("comment_id")
    private Long commentId;

    @Schema(description = "게시글 ID (작성 시 필수)", example = "15")
    @JsonProperty("post_id")
    private Long postId;

    // user_id는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.

    @Schema(description = "댓글 내용 (작성·수정 시, 255자 이하)", example = "저도 그렇게 생각해요.", maxLength = 255)
    @JsonProperty("comment_content")
    private String commentContent;

    // 대댓글을 위한 부모 댓글 ID 추가
    @Schema(description = "대댓글 작성 시 부모 댓글 ID (대댓글에는 다시 대댓글 불가)", example = "8", nullable = true)
    @JsonProperty("parent_comment_id")
    private Long parentCommentId;
}
