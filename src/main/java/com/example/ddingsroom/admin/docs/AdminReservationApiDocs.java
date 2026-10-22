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

import java.time.LocalDate;

@Tag(name = SwaggerConfig.TAG_ADMIN_RESERVATION)
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public interface AdminReservationApiDocs {

    @Operation(operationId = "adminGetReservations", summary = "전체 예약 목록", description = "취소된 예약을 포함한 모든 예약을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.ReservationListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getAllReservations();

    @Operation(operationId = "adminGetReservation", summary = "예약 상세")
    @Parameter(name = "reservationId", in = ParameterIn.PATH, description = "예약 ID", example = "26")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.ReservationDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "예약 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getReservationById(Long reservationId);

    @Operation(operationId = "adminForceCancelReservation", summary = "예약 강제 취소", description = "이용 중·종료된 예약도 취소할 수 있습니다.")
    @Parameter(name = "reservationId", in = ParameterIn.PATH, description = "예약 ID", example = "26")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID 또는 이미 취소된 예약", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "예약 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<AdminResponseDTO> forceCancelReservation(Long reservationId);

    @Operation(operationId = "adminGetReservationsByDate", summary = "날짜별 예약", description = "시작 일시가 해당 날짜인 예약을 조회합니다.")
    @Parameter(name = "date", in = ParameterIn.QUERY, required = true, description = "조회할 날짜 (yyyy-MM-dd)", example = "2026-10-21")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.ReservationsByDateResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getReservationsByDate(LocalDate date);

    @Operation(operationId = "adminGetReservationsByDateRange", summary = "기간별 예약", description = "시작 일시가 기간 안에 있는 예약을 조회합니다.")
    @Parameter(name = "startDate", in = ParameterIn.QUERY, required = true, description = "시작 날짜 (yyyy-MM-dd)", example = "2026-10-01")
    @Parameter(name = "endDate", in = ParameterIn.QUERY, required = true, description = "종료 날짜 (yyyy-MM-dd)", example = "2026-10-31")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.ReservationsByDateRangeResponse.class))),
            @ApiResponse(responseCode = "400", description = "시작 날짜가 종료 날짜보다 늦음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getReservationsByDateRange(LocalDate startDate, LocalDate endDate);

    @Operation(operationId = "adminGetReservationStatistics", summary = "예약 통계")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.ReservationStatisticsResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getReservationStatistics();

    @Operation(operationId = "adminGetDailyReservationStatistics", summary = "일별 예약 통계", description = "날짜를 생략하면 오늘 기준입니다.")
    @Parameter(name = "date", in = ParameterIn.QUERY, description = "조회할 날짜 (yyyy-MM-dd, 선택)", example = "2026-10-21")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.DailyReservationStatisticsResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getDailyReservationStatistics(LocalDate date);

    @Operation(operationId = "adminGetReservationsByUser", summary = "특정 사용자의 예약 목록")
    @Parameter(name = "userId", in = ParameterIn.PATH, description = "사용자 ID", example = "10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminDocSchemas.UserReservationsResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdminResponseDTO.class)))
    })
    ResponseEntity<?> getReservationsByUserId(Long userId);
}
