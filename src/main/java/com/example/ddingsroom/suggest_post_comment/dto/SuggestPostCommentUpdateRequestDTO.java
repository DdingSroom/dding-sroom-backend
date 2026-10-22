package com.example.ddingsroom.suggest_post_comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostCommentUpdateRequestDTO {

    @Schema(description = "수정할 답변 ID", example = "1")
    @NotNull(message = "댓글 ID는 필수입니다.")
    @JsonProperty("comment_id")
    private Long commentId;

    @Schema(description = "답변 내용", example = "채워두었습니다. 감사합니다.")
    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    @Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다.")
    @JsonProperty("answer_content")
    private String answerContent;

}
