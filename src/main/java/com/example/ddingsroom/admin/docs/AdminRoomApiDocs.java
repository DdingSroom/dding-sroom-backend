package com.example.ddingsroom.admin.docs;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminRoomRequestDTO;
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

@Tag(name = SwaggerConfig.TAG_ADMIN_ROOM)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface AdminRoomApiDocs {

    @Operation(operationId = "adminGetRooms", summary = "전체 스터디룸 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.RoomListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getAllRooms();

    @Operation(operationId = "adminGetRoom", summary = "스터디룸 상세")
    @Parameter(name = "roomId", in = ParameterIn.PATH, description = "스터디룸 ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.RoomDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "스터디룸 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getRoomById(Long roomId);

    @Operation(operationId = "adminCreateRoom", summary = "스터디룸 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "이름 누락·중복 또는 잘못된 상태 값", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> createRoom(AdminRoomRequestDTO roomRequestDTO);

    @Operation(operationId = "adminUpdateRoom", summary = "스터디룸 수정")
    @Parameter(name = "roomId", in = ParameterIn.PATH, description = "스터디룸 ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID, 이름 누락·중복 또는 잘못된 상태 값", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "스터디룸 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> updateRoom(Long roomId, AdminRoomRequestDTO roomRequestDTO);

    @Operation(operationId = "adminUpdateRoomStatus", summary = "스터디룸 상태 변경", description = "IDLE이 아닌 방은 예약할 수 없습니다.")
    @Parameter(name = "roomId", in = ParameterIn.PATH, description = "스터디룸 ID", example = "1")
    @Parameter(name = "status", in = ParameterIn.QUERY, required = true, description = "변경할 상태",
            schema = @Schema(type = "string", allowableValues = {"IDLE", "OCCUPIED", "MAINTENANCE"}))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID 또는 상태 값", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "스터디룸 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> updateRoomStatus(Long roomId, String status);

    @Operation(operationId = "adminDeleteRoom", summary = "스터디룸 삭제",
            description = "예약(RESERVED)이 있거나 취소된 예약 이력이 남아 있는 방은 삭제할 수 없습니다. 사용을 멈추려면 상태를 MAINTENANCE로 바꿉니다.")
    @Parameter(name = "roomId", in = ParameterIn.PATH, description = "스터디룸 ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID, 활성 예약 있음, 예약 이력 있음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "스터디룸 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "삭제 실패", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> deleteRoom(Long roomId);

    @Operation(operationId = "adminGetRoomStatistics", summary = "스터디룸 통계")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.RoomStatisticsResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getRoomStatistics();

    @Operation(operationId = "adminGetRoomReservations", summary = "스터디룸별 예약 목록")
    @Parameter(name = "roomId", in = ParameterIn.PATH, description = "스터디룸 ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.RoomReservationsResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "스터디룸 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getRoomReservations(Long roomId);
}
