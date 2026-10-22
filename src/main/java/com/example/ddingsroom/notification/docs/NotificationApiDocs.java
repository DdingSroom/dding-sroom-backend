package com.example.ddingsroom.notification.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.notification.dto.BaseResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationCreateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationReadRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationReadResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationUpdateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationViewCountRequestDTO;
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

@Tag(name = SwaggerConfig.TAG_NOTIFICATION)
public interface NotificationApiDocs {

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "createNotification", summary = "공지 작성 (관리자)",
            description = "관리자 토큰이 필요합니다. 권한이 없거나 실패해도 HTTP 200이며 `error`에 사유가 담깁니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공 시 data에 작성된 공지, 실패 시 error "
            + "(`관리자 권한이 필요합니다.`, `인증 정보를 확인할 수 없습니다.`, `제목은 필수 입력 항목입니다.`)"))
    ResponseEntity<BaseResponseDTO<NotificationResponseDTO>> createNotification(NotificationCreateRequestDTO dto);

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "updateNotification", summary = "공지 수정 (관리자)",
            description = "관리자 토큰이 필요합니다. 실패해도 HTTP 200이며 `error`에 사유가 담깁니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공 시 data에 수정된 공지, 실패 시 error "
            + "(`관리자 권한이 필요합니다.`, `공지사항을 찾을 수 없습니다.`)"))
    ResponseEntity<BaseResponseDTO<NotificationResponseDTO>> updateNotification(NotificationUpdateRequestDTO dto);

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "deleteNotification", summary = "공지 삭제 (관리자)",
            description = "관리자 토큰이 필요합니다. 공지의 읽음 기록도 함께 삭제됩니다. 실패해도 HTTP 200이며 `error`에 사유가 담깁니다.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "공지 ID", example = "3")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공 시 message만, 실패 시 error "
            + "(`관리자 권한이 필요합니다.`, `삭제하려는 공지사항이 존재하지 않습니다.`)"))
    ResponseEntity<BaseResponseDTO<Void>> deleteNotification(Long id);

    @Operation(operationId = "getNotifications", summary = "공지 목록",
            description = "최신순으로 전체 공지를 조회합니다. 로그인 토큰을 보내면 각 공지의 `isRead`에 본인 읽음 여부가 들어가고, "
                    + "토큰이 없으면 모두 false입니다. 안 읽은 공지 수는 `isRead=false` 항목을 세면 됩니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 공지 목록"))
    ResponseEntity<BaseResponseDTO<List<NotificationResponseDTO>>> getAllNotifications();

    @Operation(operationId = "getNotification", summary = "공지 상세",
            description = "로그인 토큰을 보내면 `isRead`에 본인 읽음 여부가 들어갑니다. 상세 조회만으로는 읽음 처리되지 않습니다.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "공지 ID", example = "3")
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "data에 공지, 없으면 error (`해당 공지사항을 찾을 수 없습니다. ID: {id}`)"))
    ResponseEntity<BaseResponseDTO<NotificationResponseDTO>> getNotification(Long id);

    @Operation(operationId = "increaseNotificationViewCount", summary = "공지 조회수 증가",
            description = "호출할 때마다 조회수가 1 증가합니다. 읽음 처리와는 별개입니다.")
    @ApiResponses(@ApiResponse(responseCode = "200",
            description = "성공 시 message만, 실패 시 error (`공지사항을 찾을 수 없습니다.`)"))
    ResponseEntity<BaseResponseDTO<Void>> incrementViewCount(NotificationViewCountRequestDTO dto);

    @Operation(operationId = "getRecentNotificationCount", summary = "최근 3일 공지 수",
            description = "최근 3일 안에 작성된 공지 수를 data에 담아 반환합니다. (읽음 여부와 무관)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "data에 공지 수"))
    ResponseEntity<BaseResponseDTO<Long>> getRecentNotificationsCount();

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "markNotificationAsRead", summary = "공지 읽음 처리",
            description = "로그인한 사용자 기준으로 공지를 읽음 처리합니다. 여러 번 호출해도 결과가 같고 `readAt`은 처음 읽은 시각으로 유지됩니다. "
                    + "이 API는 결과를 HTTP 상태코드로 구분합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공 (이미 읽은 공지 포함)"),
            @ApiResponse(responseCode = "400", description = "notificationId 누락", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotificationDocSchemas.ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": null, \"error\": \"공지사항 ID는 필수입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "404", description = "공지 또는 사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotificationDocSchemas.ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": null, \"error\": \"공지사항을 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotificationDocSchemas.ErrorResponse.class)))
    })
    ResponseEntity<BaseResponseDTO<NotificationReadResponseDTO>> markAsRead(NotificationReadRequestDTO dto);
}
