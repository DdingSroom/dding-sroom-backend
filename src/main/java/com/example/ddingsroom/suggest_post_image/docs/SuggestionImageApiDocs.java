package com.example.ddingsroom.suggest_post_image.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.suggest_post.docs.SuggestionDocSchemas;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageUploadRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = SwaggerConfig.TAG_SUGGESTION_IMAGE)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface SuggestionImageApiDocs {

    @Operation(operationId = "uploadSuggestionImage", summary = "건의 이미지 업로드",
            description = "multipart/form-data로 `request` 파트(JSON: `{\"suggest_post_id\": 10}`)와 `image_file` 파트(이미지, 10MB 이하)를 보냅니다. "
                    + "건의 작성자만 올릴 수 있습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ImageUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "건의 없음, 작성자가 아님, 빈 파일, 입력값 검증 실패(필드별 메시지)", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {SuggestionDocSchemas.ErrorResponse.class,
                            SuggestionDocSchemas.ValidationErrorResponse.class}),
                    examples = @ExampleObject(value = "{\"error\": \"게시물 작성자만 이미지를 업로드할 수 있습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "업로드 실패", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, Object>> uploadSuggestImage(SuggestPostImageUploadRequestDTO request, MultipartFile imageFile);

    @Operation(operationId = "deleteSuggestionImage", summary = "건의 이미지 삭제", description = "건의 작성자만 삭제할 수 있습니다.")
    @Parameter(name = "imageId", in = ParameterIn.PATH, description = "이미지 ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.MessageResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"건의 이미지가 성공적으로 삭제되었습니다!\"}"))),
            @ApiResponse(responseCode = "400", description = "이미지 없음 또는 작성자가 아님", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, String>> deleteSuggestImage(Long imageId);

    @Operation(operationId = "getSuggestionImages", summary = "건의 이미지 목록")
    @Parameter(name = "suggest_post_id", in = ParameterIn.QUERY, required = true, description = "건의 ID", example = "10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ImageListResponse.class))),
            @ApiResponse(responseCode = "400", description = "건의 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SuggestionDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<Map<String, Object>> retrieveSuggestImages(Long suggestPostId);
}
