package com.example.ddingsroom.community_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 작성·수정·삭제 요청 (API마다 필요한 필드가 다름)")
public class CommunityPostRequestDTO {

    @Schema(description = "게시글 ID (수정·삭제 시 필수)", example = "15")
    @JsonProperty("post_id")
    private Long postId;        // update, delete용

    // user_id는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.

    @Schema(description = "제목 (작성·수정 시, 255자 이하)", example = "스터디룸 이용 후기", maxLength = 255)
    @JsonProperty("title")
    private String title;

    @Schema(description = "본문 (작성·수정 시, 255자 이하)", example = "조용하고 좋았어요.", maxLength = 255)
    @JsonProperty("content")
    private String content;

    @Schema(description = "게시판 분류 번호 (작성·수정 시)", example = "1")
    @JsonProperty("category")
    private Integer category;   // API 문서에서 "..."로 되어있지만 테이블에서 int이므로 Integer 사용
}
