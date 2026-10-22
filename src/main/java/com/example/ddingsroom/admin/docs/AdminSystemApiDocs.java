package com.example.ddingsroom.admin.docs;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = SwaggerConfig.TAG_ADMIN_SYSTEM)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface AdminSystemApiDocs {

    @Operation(operationId = "adminGetDashboard", summary = "대시보드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.DashboardResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getAdminDashboard();

    @Operation(operationId = "adminGetSystemStatistics", summary = "전체 시스템 통계")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.SystemStatisticsResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getSystemStatistics();

    @Operation(operationId = "adminGetSystemHealth", summary = "시스템 상태")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.SystemHealthResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getSystemHealth();
}
