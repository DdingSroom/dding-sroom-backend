package com.example.ddingsroom.suggest_post.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.suggest_post.dto.SuggestPostCreateRequestDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostUpdateRequestDTO;
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
import java.util.Optional;

@Tag(name = SwaggerConfig.TAG_SUGGESTION)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface SuggestionApiDocs {

    @Operation(operationId = "createSuggestion", summary = "건의 작성",
            description = "카테고리·위치는 한글 이름으로 보냅니다. 이미지는 작성 후 `/api/suggestions/images`로 따로 올립니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작성 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패(필드별 메시지) 또는 카테고리·위치 이름 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {SuggestionDocSchemas.ErrorResponse.class, SuggestionDocSchemas.ValidationErrorResponse.class}),
                    examples = {
                            @ExampleObject(name = "이름 오류", value = "{\"error\": \"유효하지 않은 카테고리 값입니다: '청소'. 유효한 값: 미예약 사용자 신고, 분실물, 기물 파손, 시설 고장, 소음 공해, 기타\"}"),
                            @ExampleObject(name = "검증 실패", value = "{\"suggestTitle\": \"건의 제목은 필수입니다.\"}")})),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> createSuggestPost(SuggestPostCreateRequestDTO request);

    @Operation(operationId = "updateSuggestion", summary = "건의 수정", description = "작성자만 수정할 수 있습니다. 모든 필드를 함께 보냅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"건의가 성공적으로 업데이트되었습니다!\"}"))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패(필드별 메시지), 건의 없음, 카테고리·위치 이름 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {SuggestionDocSchemas.ErrorResponse.class, SuggestionDocSchemas.ValidationErrorResponse.class}),
                    examples = {
                            @ExampleObject(name = "건의 없음", value = "{\"error\": \"건의 게시물을 찾을 수 없습니다. ID: 99\"}"),
                            @ExampleObject(name = "검증 실패", value = "{\"isAnswered\": \"답변 여부는 필수입니다.\"}")})),
            @ApiResponse(responseCode = "403", description = "작성자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"게시물 수정 권한이 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> updateSuggestPost(SuggestPostUpdateRequestDTO request);

    @Operation(operationId = "deleteSuggestion", summary = "건의 삭제", description = "작성자만 삭제할 수 있습니다. 답변과 첨부 이미지(S3 파일 포함)도 함께 삭제됩니다.")
    @Parameter(name = "suggestId", in = ParameterIn.PATH, description = "건의 ID", example = "10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"건의가 성공적으로 삭제되었습니다!\"}"))),
            @ApiResponse(responseCode = "400", description = "건의 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"게시물 삭제 권한이 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> deleteSuggestPost(Long suggestId);

    @Operation(operationId = "getSuggestions", summary = "건의 목록",
            description = "조건을 조합해 조회합니다(모두 선택). 조건이 없으면 전체를 반환합니다.")
    @Parameter(name = "suggest_id", in = ParameterIn.QUERY, description = "건의 ID", example = "10")
    @Parameter(name = "user_id", in = ParameterIn.QUERY, description = "작성자 ID", example = "15")
    @Parameter(name = "category", in = ParameterIn.QUERY, description = "카테고리 이름",
            schema = @Schema(type = "string", allowableValues = {"미예약 사용자 신고", "분실물", "기물 파손", "시설 고장", "소음 공해", "기타"}))
    @Parameter(name = "location", in = ParameterIn.QUERY, description = "위치 이름",
            schema = @Schema(type = "string", allowableValues = {"스터디룸1", "스터디룸2", "스터디룸3", "스터디룸4", "스터디룸5"}))
    @Parameter(name = "is_answered", in = ParameterIn.QUERY, description = "답변 완료 여부", example = "false")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ListResponse.class))),
            @ApiResponse(responseCode = "400", description = "카테고리·위치 이름 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, Object>> retrieveSuggestPosts(Optional<Long> suggestId, Optional<Long> userId,
                                                             Optional<String> category, Optional<String> location,
                                                             Optional<Boolean> isAnswered);
}
