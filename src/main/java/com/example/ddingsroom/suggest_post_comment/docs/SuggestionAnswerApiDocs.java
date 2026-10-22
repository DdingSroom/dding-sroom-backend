package com.example.ddingsroom.suggest_post_comment.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.suggest_post.docs.SuggestionDocSchemas;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentCreateRequestDTO;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Tag(name = SwaggerConfig.TAG_SUGGESTION_ANSWER)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface SuggestionAnswerApiDocs {

    @Operation(operationId = "createSuggestionAnswer", summary = "건의 답변 작성 (관리자)",
            description = "건의당 답변은 1개이며, 작성하면 건의의 `is_answered`가 true가 됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작성 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"건의 댓글이 성공적으로 생성되었습니다!\"}"))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패(필드별 메시지) 또는 건의 없음·이미 답변 있음", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {SuggestionDocSchemas.ErrorResponse.class,
                                    SuggestionDocSchemas.ValidationErrorResponse.class}),
                            examples = {
                                    @ExampleObject(name = "이미 답변 있음", value = "{\"error\": \"해당 건의 게시물(ID: 10)에는 이미 댓글이 존재합니다.\"}"),
                                    @ExampleObject(name = "검증 실패", value = "{\"answerContent\": \"댓글 내용은 비워둘 수 없습니다.\"}")})}),
            @ApiResponse(responseCode = "403", description = "관리자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"댓글 생성 권한이 없습니다. (관리자 전용 기능)\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> createSuggestComment(SuggestPostCommentCreateRequestDTO request);

    @Operation(operationId = "updateSuggestionAnswer", summary = "건의 답변 수정 (관리자)", description = "답변을 작성한 관리자 본인만 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"건의 댓글이 성공적으로 업데이트되었습니다!\"}"))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패(필드별 메시지), 답변 없음, 작성자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {SuggestionDocSchemas.ErrorResponse.class,
                            SuggestionDocSchemas.ValidationErrorResponse.class}))),
            @ApiResponse(responseCode = "403", description = "관리자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> updateSuggestComment(SuggestPostCommentUpdateRequestDTO request);

    @Operation(operationId = "deleteSuggestionAnswer", summary = "건의 답변 삭제 (관리자)",
            description = "답변을 작성한 관리자 본인만 삭제할 수 있고, 삭제하면 건의의 `is_answered`가 false가 됩니다.")
    @Parameter(name = "commentId", in = ParameterIn.PATH, description = "답변 ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"건의 댓글이 성공적으로 삭제되었습니다!\"}"))),
            @ApiResponse(responseCode = "400", description = "답변 없음 또는 작성자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> deleteSuggestComment(Long commentId);

    @Operation(operationId = "getSuggestionAnswers", summary = "건의 답변 조회")
    @Parameter(name = "suggest_post_id", in = ParameterIn.QUERY, required = true, description = "건의 ID", example = "10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.AnswerListResponse.class))),
            @ApiResponse(responseCode = "400", description = "건의 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, Object>> retrieveSuggestComments(Long postId);
}
