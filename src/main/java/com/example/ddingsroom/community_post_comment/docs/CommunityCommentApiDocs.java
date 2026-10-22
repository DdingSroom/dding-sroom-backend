package com.example.ddingsroom.community_post_comment.docs;

import com.example.ddingsroom.community_post_comment.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post_comment.dto.CommunityPostCommentRequestDTO;
import com.example.ddingsroom.community_post_comment.dto.CommunityPostCommentResponseDTO;
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

@Tag(name = SwaggerConfig.TAG_COMMUNITY_COMMENT)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface CommunityCommentApiDocs {

    @Operation(operationId = "createComment", summary = "댓글·대댓글 작성",
            description = "`post_id`, `comment_content`를 보냅니다. 대댓글은 `parent_comment_id`를 함께 보내며 "
                    + "대댓글에는 다시 대댓글을 달 수 없습니다. 실패해도 HTTP 200이며 `error`에 사유가 담깁니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityPostCommentRequestDTO.class),
            examples = {
                    @ExampleObject(name = "댓글", value = "{\"post_id\": 15, \"comment_content\": \"좋은 정보 감사합니다.\"}"),
                    @ExampleObject(name = "대댓글", value = "{\"post_id\": 15, \"comment_content\": \"저도요!\", \"parent_comment_id\": 8}")}))
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공 시 data에 작성된 댓글, 실패 시 error "
            + "(`존재하지 않는 게시글입니다.`, `부모 댓글을 찾을 수 없습니다.`, `대댓글에는 더 이상 댓글을 달 수 없습니다.`)"))
    ResponseEntity<BaseResponseDTO<CommunityPostCommentResponseDTO>> createComment(CommunityPostCommentRequestDTO dto);

    @Operation(operationId = "updateComment", summary = "댓글 수정", description = "`comment_id`, `comment_content`를 보냅니다. 작성자만 수정할 수 있습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityPostCommentRequestDTO.class),
            examples = @ExampleObject(value = "{\"comment_id\": 8, \"comment_content\": \"수정한 댓글\"}")))
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "성공 시 data에 수정된 댓글, 실패 시 error (`댓글을 찾을 수 없습니다.`, `작성자만 수정할 수 있습니다.`)"))
    ResponseEntity<BaseResponseDTO<CommunityPostCommentResponseDTO>> updateComment(CommunityPostCommentRequestDTO dto);

    @Operation(operationId = "deleteComment", summary = "댓글 삭제",
            description = "DELETE 요청 본문에 `comment_id`를 보냅니다. 대댓글이 달린 댓글은 삭제할 수 없습니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommunityPostCommentRequestDTO.class),
            examples = @ExampleObject(value = "{\"comment_id\": 8}")))
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공 시 message만, 실패 시 error "
            + "(`삭제하려는 댓글이 존재하지 않습니다.`, `작성자만 삭제할 수 있습니다.`, `대댓글이 있는 댓글은 삭제할 수 없습니다. 먼저 대댓글을 삭제해주세요.`)"))
    ResponseEntity<BaseResponseDTO<Void>> deleteComment(CommunityPostCommentRequestDTO dto);

    @Operation(operationId = "getCommentsByPost", summary = "게시글의 댓글 목록 (계층형)",
            description = "일반 댓글 목록을 반환하고, 각 댓글의 `replies`에 대댓글이 들어 있습니다.")
    @Parameter(name = "postId", in = ParameterIn.PATH, description = "게시글 ID", example = "15")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 댓글 목록"))
    ResponseEntity<BaseResponseDTO<List<CommunityPostCommentResponseDTO>>> getCommentsByPostId(Long postId);

    @Operation(operationId = "getReplies", summary = "댓글의 대댓글 목록")
    @Parameter(name = "commentId", in = ParameterIn.PATH, description = "부모 댓글 ID", example = "8")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 대댓글 목록"))
    ResponseEntity<BaseResponseDTO<List<CommunityPostCommentResponseDTO>>> getRepliesByCommentId(Long commentId);

    @Operation(operationId = "getCommentsByUser", summary = "특정 사용자의 댓글 목록",
            description = "`type`으로 일반 댓글/대댓글만, `postId`로 특정 게시글만 걸러낼 수 있습니다.")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "작성자 ID", example = "10")
    @Parameter(name = "type", in = ParameterIn.QUERY, description = "comment: 일반 댓글만, reply: 대댓글만 (생략 시 전체)",
            schema = @Schema(type = "string", allowableValues = {"comment", "reply"}))
    @Parameter(name = "postId", in = ParameterIn.QUERY, description = "특정 게시글 ID (선택)", example = "15")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 댓글 목록"))
    ResponseEntity<BaseResponseDTO<List<CommunityPostCommentResponseDTO>>> getCommentsByUserId(Long userId, String type, Long postId);
}
