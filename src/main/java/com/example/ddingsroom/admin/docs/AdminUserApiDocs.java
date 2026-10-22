package com.example.ddingsroom.admin.docs;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = SwaggerConfig.TAG_ADMIN_USER)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface AdminUserApiDocs {

    @Operation(operationId = "adminGetUsers", summary = "전체 사용자 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.UserListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getAllUsers();

    @Operation(operationId = "adminGetUser", summary = "사용자 상세")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "사용자 ID", example = "10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.UserDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getUserById(Long userId);

    @Operation(operationId = "adminUpdateUserStatus", summary = "사용자 상태 변경",
            description = "계정 상태 값을 normal 또는 blocked로 바꿉니다. blocked로 바꾸면 해당 사용자의 refresh 토큰이 삭제되어 재발급이 막힙니다. "
                    + "로그인 자체는 막지 않으므로 다시 로그인하면 이용할 수 있습니다.")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "사용자 ID", example = "10")
    @Parameter(name = "status", in = ParameterIn.QUERY, required = true, description = "변경할 상태",
            schema = @Schema(type = "string", allowableValues = {"normal", "blocked"}))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID 또는 상태 값", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> updateUserStatus(Long userId, String status);

    @Operation(operationId = "adminUpdateUserRole", summary = "사용자 권한 변경",
            description = "권한이 바뀌면 해당 사용자의 refresh 토큰이 삭제되어, 다시 로그인한 뒤 새 권한이 토큰에 반영됩니다. "
                    + "이미 발급된 access 토큰은 만료될 때까지(최대 1시간) 이전 권한으로 동작합니다.")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "사용자 ID", example = "10")
    @Parameter(name = "role", in = ParameterIn.QUERY, required = true, description = "변경할 권한",
            schema = @Schema(type = "string", allowableValues = {"ROLE_USER", "ROLE_ADMIN"}))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID 또는 권한 값", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> updateUserRole(Long userId, String role);

    @Operation(operationId = "adminDeleteUser", summary = "사용자 삭제",
            description = "사용자와 관련 데이터(예약·게시글·댓글·건의·답변·공지·읽음 기록·refresh 토큰·첨부 이미지 파일)를 함께 삭제합니다.")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "사용자 ID", example = "10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> deleteUser(Long userId);

    @Operation(operationId = "adminGetUserStatistics", summary = "사용자 통계")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.UserStatisticsResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getUserStatistics();
}
