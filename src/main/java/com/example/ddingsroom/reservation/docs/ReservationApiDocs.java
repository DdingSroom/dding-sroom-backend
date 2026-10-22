package com.example.ddingsroom.reservation.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.reservation.dto.BaseResponseDTO;
import com.example.ddingsroom.reservation.dto.ReservationCancelRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = SwaggerConfig.TAG_RESERVATION)
public interface ReservationApiDocs {

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "createReservation", summary = "예약 생성",
            description = """
                    로그인한 사용자로 스터디룸을 예약합니다.
                    - 1시간 또는 2시간 단위, 과거 시간 불가, 사용자별 하루(시작일 기준) 합계 2시간 이하
                    - 같은 시간대에 다른 방 중복 예약 불가, 같은 방 연속 예약 불가
                    - 성공 메시지는 `예약이 성공적으로 완료되었습니다.`

                    ⚠️ 현재 다음 실패도 **HTTP 201**로 응답하므로 `message`가 성공 메시지와 다르면 실패로 처리해야 합니다:
                    `예약은 1시간 또는 2시간 단위로만 가능합니다.`, `과거 시간으로 예약할 수 없습니다.`,
                    `사용자별 예약은 하루 2시간으로 제한됩니다.(…)`, `해당 스터디룸이 존재하지 않습니다.`, `해당 사용자가 존재하지 않습니다.`,
                    `예약 시스템 오류가 발생했습니다. 나중에 다시 시도해주세요.`, `예약 처리 중 오류가 발생했습니다. 나중에 다시 시도해주세요.`
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 성공 (일부 실패도 201, 설명 참고)", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "성공", value = "{\"message\": \"예약이 성공적으로 완료되었습니다.\"}"),
                            @ExampleObject(name = "하루 2시간 초과", value = "{\"message\": \"사용자별 예약은 하루 2시간으로 제한됩니다.(남은 시간: 1시간)\"}")})),
            @ApiResponse(responseCode = "400", description = "예약 불가 (이미 예약됨, 중복·연속 예약, 사용 불가 방) 또는 요청 형식 오류",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "사용 불가", value = "{\"message\": \"현재 사용이 불가능한 스터디룸입니다.\"}"),
                                    @ExampleObject(name = "다른 방 중복", value = "{\"message\": \"동일한 시간대에 다른 스터디룸 예약이 불가능합니다.\"}"),
                                    @ExampleObject(name = "연속 예약", value = "{\"message\": \"같은 스터디룸에서 연속된 시간 예약은 불가능합니다.\"}")})),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class)))
    })
    ResponseEntity<BaseResponseDTO> createReservation(ReservationRequestDTO requestDTO);

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "cancelReservation", summary = "예약 취소",
            description = """
                    본인 예약을 취소합니다. 성공 메시지는 `예약취소가 잘 되었습니다.`

                    ⚠️ 다음 실패도 **HTTP 200**으로 응답하므로 `message`가 성공 메시지와 다르면 실패로 처리해야 합니다:
                    `이용 중인 예약은 취소할 수 없습니다.`, `이용이 완료된 예약은 취소할 수 없습니다.`,
                    `예약 취소 중 오류가 발생했습니다. 나중에 다시 시도해주세요.`
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공 (일부 실패도 200, 설명 참고)", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"예약취소가 잘 되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "남의 예약, 이미 취소됨, 예약 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "남의 예약", value = "{\"message\": \"본인의 예약만 취소할 수 있습니다.\"}"),
                            @ExampleObject(name = "이미 취소", value = "{\"message\": \"이미 취소된 예약입니다.\"}"),
                            @ExampleObject(name = "예약 없음", value = "{\"message\": \"예약을 찾을 수 없습니다.\"}")})),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class)))
    })
    ResponseEntity<BaseResponseDTO> cancelReservation(ReservationCancelRequestDTO requestDTO);

    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "getMyReservations", summary = "내 예약 목록",
            description = "로그인한 사용자의 예약을 생성일 최신순으로 최대 20건 조회합니다. 취소된 예약도 포함됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과 (조회 중 오류도 200, 빈 목록과 오류 메시지)"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"서버 오류가 발생했습니다. 나중에 다시 시도해주세요.\"}")))
    })
    ResponseEntity<ReservationResponseDTO> getMyReservations();

    @Operation(operationId = "getAllReservations", summary = "전체 예약 목록",
            description = "모든 사용자의 예약을 조회합니다(취소 포함, 페이징 없음). 예약 가능 시간 계산에 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과 (조회 중 오류도 200, 빈 목록과 오류 메시지)"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"서버 오류가 발생했습니다. 나중에 다시 시도해주세요.\"}")))
    })
    ResponseEntity<ReservationResponseDTO> getAllReservations();

    @Operation(operationId = "getCrowdingLevel", summary = "실시간 혼잡도",
            description = "지금 이용 중인 스터디룸 수를 `message`에 숫자 문자열로 반환합니다. "
                    + "조회 중 오류가 나도 HTTP 200이며 이때 `message`는 숫자가 아닌 `혼잡도 조회 중 오류가 발생했습니다.`입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 결과 (오류 시에도 200, 숫자가 아닌 오류 메시지)", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"3\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"서버 오류가 발생했습니다. 나중에 다시 시도해주세요.\"}")))
    })
    ResponseEntity<BaseResponseDTO> getCurrentCrowdingLevel();
}
