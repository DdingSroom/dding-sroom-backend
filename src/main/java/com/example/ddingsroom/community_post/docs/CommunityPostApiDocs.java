package com.example.ddingsroom.community_post.docs;

import com.example.ddingsroom.community_post.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostRequestDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import com.example.ddingsroom.config.SwaggerConfig;
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

import java.util.List;
import java.util.Map;

@Tag(name = SwaggerConfig.TAG_COMMUNITY_POST)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface CommunityPostApiDocs {

    @Operation(operationId = "createCommunityPost", summary = "게시글 작성",
            description = "`title`, `content`, `category`를 보냅니다. 실패해도 HTTP 200이며 `error`에 사유가 담깁니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityPostRequestDTO.class),
            examples = @ExampleObject(value = "{\"title\": \"스터디룸 이용 후기\", \"content\": \"조용하고 좋았어요.\", \"category\": 1}")))
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공 시 data에 작성된 게시글, 실패 시 error"))
    ResponseEntity<BaseResponseDTO<CommunityPostResponseDTO>> createCommunityPost(CommunityPostRequestDTO dto);

    @Operation(operationId = "updateCommunityPost", summary = "게시글 수정",
            description = "`post_id`와 수정할 `title`, `content`, `category`를 보냅니다. 작성자만 수정할 수 있습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityPostRequestDTO.class),
            examples = @ExampleObject(value = "{\"post_id\": 15, \"title\": \"수정한 제목\", \"content\": \"수정한 내용\", \"category\": 1}")))
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "성공 시 data에 수정된 게시글, 실패 시 error (`게시글을 찾을 수 없습니다.`, `작성자만 수정할 수 있습니다.`)"))
    ResponseEntity<BaseResponseDTO<CommunityPostResponseDTO>> updateCommunityPost(CommunityPostRequestDTO dto);

    @Operation(operationId = "deleteCommunityPost", summary = "게시글 삭제",
            description = "DELETE 요청 본문에 `post_id`를 보냅니다. 작성자만 삭제할 수 있고 달린 댓글도 함께 삭제됩니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityPostRequestDTO.class),
            examples = @ExampleObject(value = "{\"post_id\": 15}")))
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "성공 시 message만, 실패 시 error (`삭제하려는 게시글이 존재하지 않습니다.`, `작성자만 삭제할 수 있습니다.`)"))
    ResponseEntity<BaseResponseDTO<Void>> deleteCommunityPost(CommunityPostRequestDTO dto);

    @Operation(operationId = "searchCommunityPosts", summary = "게시글 조건 조회",
            description = "`post_id` > (`user_id` + `category`) > `user_id` > `category` 순으로 하나의 조건만 적용됩니다. "
                    + "조건이 없으면 전체를 반환합니다.")
    @Parameter(name = "post_id", in = ParameterIn.QUERY, description = "게시글 ID", example = "15")
    @Parameter(name = "user_id", in = ParameterIn.QUERY, description = "작성자 ID", example = "10")
    @Parameter(name = "category", in = ParameterIn.QUERY, description = "게시판 분류 번호", example = "1")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CommunityPostDocSchemas.SearchResponse.class))))
    ResponseEntity<Map<String, Object>> retrieveCommunityPost(Long post_id, Long user_id, Integer category);

    @Operation(operationId = "getCommunityPosts", summary = "게시글 전체 목록", description = "모든 게시글을 조회합니다. (정렬 없음)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 게시글 목록"))
    ResponseEntity<BaseResponseDTO<List<CommunityPostResponseDTO>>> getAllCommunityPosts();

    @Operation(operationId = "getCommunityPost", summary = "게시글 상세")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "게시글 ID", example = "15")
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "data에 게시글, 없으면 error (`해당 게시글을 찾을 수 없습니다. ID: {id}`)"))
    ResponseEntity<BaseResponseDTO<CommunityPostResponseDTO>> getCommunityPost(Long id);

    @Operation(operationId = "getMyCommunityPosts", summary = "내 게시글 목록",
            description = "토큰의 사용자 기준으로 조회합니다. `category`를 주면 해당 분류만 조회합니다.")
    @Parameter(name = "category", in = ParameterIn.QUERY, description = "게시판 분류 번호 (선택)", example = "1")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 게시글 목록"))
    ResponseEntity<BaseResponseDTO<List<CommunityPostResponseDTO>>> getMyPosts(Integer category);

    @Operation(operationId = "getCommunityPostsByUser", summary = "특정 사용자의 게시글 목록")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "작성자 ID", example = "10")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 게시글 목록"))
    ResponseEntity<BaseResponseDTO<List<CommunityPostResponseDTO>>> getPostsByUserId(Long userId);
}
