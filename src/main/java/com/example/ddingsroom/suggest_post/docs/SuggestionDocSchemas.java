package com.example.ddingsroom.suggest_post.docs;

import com.example.ddingsroom.suggest_post.dto.SuggestPostResponseDTO;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentResponseDTO;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// 건의 API의 Map 응답을 설명하는 문서용 스키마 (실제 응답 객체로는 쓰지 않음)
public final class SuggestionDocSchemas {

    private SuggestionDocSchemas() {
    }

    @Schema(name = "SuggestionMessageResponse", description = "건의 API 성공 응답")
    public static class MessageResponse {
        @Schema(description = "결과 메시지", example = "건의가 성공적으로 생성되었습니다!", requiredMode = Schema.RequiredMode.REQUIRED)
        public String message;
    }

    @Schema(name = "SuggestionErrorResponse", description = "건의 API 실패 응답")
    public static class ErrorResponse {
        @Schema(description = "실패 사유", example = "건의 게시물을 찾을 수 없습니다. ID: 99", requiredMode = Schema.RequiredMode.REQUIRED)
        public String error;
    }

    @Schema(name = "SuggestionListResponse", description = "건의 목록. 결과가 없으면 suggestions 없이 message만 온다")
    public static class ListResponse {
        @Schema(description = "\"성공\" 또는 결과 없음 안내 (`등록된 건의가 없습니다.`, `검색 조건에 해당하는 건의를 찾을 수 없습니다.`)",
                example = "성공", requiredMode = Schema.RequiredMode.REQUIRED)
        public String message;
        @Schema(description = "건의 목록 (결과가 없으면 필드 자체가 없음)")
        public List<SuggestPostResponseDTO> suggestions;
    }

    @Schema(name = "SuggestionAnswerListResponse", description = "건의 답변 목록 (건의당 최대 1개)")
    public static class AnswerListResponse {
        @Schema(description = "\"성공\" 또는 `해당 건의에 댓글이 존재하지 않습니다.`", example = "성공",
                requiredMode = Schema.RequiredMode.REQUIRED)
        public String message;
        @Schema(description = "답변 목록 (없으면 빈 배열)", requiredMode = Schema.RequiredMode.REQUIRED)
        public List<SuggestPostCommentResponseDTO> comments;
    }

    @Schema(name = "SuggestionImageUploadResponse", description = "이미지 업로드 결과")
    public static class ImageUploadResponse {
        @Schema(description = "결과 메시지", example = "건의 이미지가 성공적으로 업로드되었습니다!", requiredMode = Schema.RequiredMode.REQUIRED)
        public String message;
        @Schema(description = "생성된 이미지 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("image_id")
        public Long imageId;
        @Schema(description = "이미지 URL (S3)", example = "https://ddingsroom-bucket.s3.ap-northeast-2.amazonaws.com/suggest-images/3f2b...-board.png",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("image_url")
        public String imageUrl;
    }

    @Schema(name = "SuggestionImageListResponse", description = "건의 첨부 이미지 목록")
    public static class ImageListResponse {
        @Schema(description = "\"성공\" 또는 `해당 건의에 첨부된 이미지가 없습니다.`", example = "성공",
                requiredMode = Schema.RequiredMode.REQUIRED)
        public String message;
        @Schema(description = "이미지 목록 (없으면 빈 배열)", requiredMode = Schema.RequiredMode.REQUIRED)
        public List<SuggestPostImageResponseDTO> images;
    }

    @Schema(name = "ValidationErrorResponse", type = "object", additionalPropertiesSchema = String.class,
            description = "입력값 검증 실패. 키는 서버 필드명(camelCase, 예: answerContent), 값은 오류 메시지",
            example = "{\"answerContent\": \"댓글 내용은 비워둘 수 없습니다.\"}")
    public static class ValidationErrorResponse {
    }
}
