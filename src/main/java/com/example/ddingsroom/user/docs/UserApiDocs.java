package com.example.ddingsroom.user.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.user.dto.ChangeUsernameDTO;
import com.example.ddingsroom.user.dto.CodeSendDTO;
import com.example.ddingsroom.user.dto.CodeVerifyDTO;
import com.example.ddingsroom.user.dto.EmailVerificationDTO;
import com.example.ddingsroom.user.dto.ModifyPasswordDTO;
import com.example.ddingsroom.user.dto.ResponseDTO;
import com.example.ddingsroom.user.dto.SignUpDTO;
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

public interface UserApiDocs {

    @Tag(name = SwaggerConfig.TAG_AUTH)
    @Operation(operationId = "modifyPassword", summary = "비밀번호 재설정",
            description = "가입한 이메일의 비밀번호를 변경합니다. 프론트에서 이메일 인증(`/user/code-send` → `/user/code-verify`)을 마친 뒤 호출하며, "
                    + "서버는 인증 완료 여부를 따로 확인하지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"비밀번호가 성공적으로 변경되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 가입되지 않은 이메일", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "이메일 형식", value = "{\"message\": \"올바른 이메일 형식이 아닙니다.\"}"),
                            @ExampleObject(name = "비밀번호 길이", value = "{\"message\": \"비밀번호는 6자 이상이어야 합니다.\"}"),
                            @ExampleObject(name = "미가입 이메일", value = "{\"message\": \"존재하지 않는 이메일입니다.\"}")})),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> modifyPassword(ModifyPasswordDTO modifyPasswordDTO);

    @Tag(name = SwaggerConfig.TAG_AUTH)
    @Operation(operationId = "sendVerificationCode", summary = "이메일 인증 코드 발송",
            description = "6자리 인증 코드를 메일로 보냅니다. 코드는 5분간 유효하며 다시 요청하면 이전 코드는 폐기됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"인증 코드가 student@mju.ac.kr로 전송되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "이메일 누락 또는 형식 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"올바른 이메일 형식이 아닙니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "메일 발송 실패", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> codeSend(CodeSendDTO codeSendDTO);

    @Tag(name = SwaggerConfig.TAG_AUTH)
    @Operation(operationId = "verifyCode", summary = "이메일 인증 코드 확인",
            description = "발송된 코드를 확인합니다. 성공하면 코드는 즉시 폐기됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"이메일 인증이 성공적으로 완료되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "코드 불일치·만료·미발송 또는 입력값 누락", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "불일치", value = "{\"message\": \"인증 코드가 일치하지 않습니다.\"}"),
                            @ExampleObject(name = "만료", value = "{\"message\": \"인증코드가 만료되었습니다.\"}"),
                            @ExampleObject(name = "미발송", value = "{\"message\": \"인증 코드가 존재하지 않거나 만료되었습니다.\"}")})),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> codeVerify(CodeVerifyDTO codeVerifyDTO);

    @Tag(name = SwaggerConfig.TAG_AUTH)
    @Operation(operationId = "signUp", summary = "회원가입", description = "일반 사용자(ROLE_USER)로 가입합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"회원가입이 성공적으로 완료되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 이메일·닉네임 중복", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "이메일 중복", value = "{\"message\": \"이미 가입된 이메일입니다.\"}"),
                            @ExampleObject(name = "닉네임 중복", value = "{\"message\": \"이미 사용 중인 사용자명입니다.\"}"),
                            @ExampleObject(name = "비밀번호 길이", value = "{\"message\": \"비밀번호는 6자 이상이어야 합니다.\"}")})),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> signUp(SignUpDTO signUpDTO);

    @Tag(name = SwaggerConfig.TAG_USER)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "changeUsername", summary = "닉네임 변경",
            description = "로그인한 사용자의 닉네임을 바꿉니다. 토큰에 들어 있는 username 값은 다시 로그인해야 갱신됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"사용자명이 성공적으로 변경되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "닉네임 누락 또는 중복", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"중복된 이름입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> changeUsername(ChangeUsernameDTO changeUsernameDTO);

    @Tag(name = SwaggerConfig.TAG_USER)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "verifyEmail", summary = "본인 이메일 확인",
            description = "입력한 이메일이 로그인한 계정의 이메일과 같은지 확인합니다. (탈퇴 전 본인 확인 등)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일치", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"사용자 이메일이 일치합니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "불일치 또는 이메일 누락", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"사용자 이메일이 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> verifyEmail(EmailVerificationDTO emailVerificationDTO);

    @Tag(name = SwaggerConfig.TAG_USER)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @Operation(operationId = "withdraw", summary = "회원 탈퇴",
            description = "로그인한 사용자를 삭제합니다. 예약·게시글·댓글·건의(첨부 이미지 파일 포함)·공지 읽음 기록 등 관련 데이터도 함께 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"message\": \"회원탈퇴가 성공적으로 완료되었습니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
    })
    ResponseEntity<ResponseDTO> withdrawUser();
}
