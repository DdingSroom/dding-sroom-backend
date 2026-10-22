package com.example.ddingsroom.user.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = SwaggerConfig.TAG_AUTH)
public interface TokenApiDocs {

    @Operation(operationId = "reissueToken", summary = "토큰 재발급",
            description = "`refresh` 쿠키로 액세스·리프레시 토큰을 모두 새로 발급합니다. 기존 리프레시 토큰은 폐기됩니다. "
                    + "응답 본문은 없고 헤더 `access`와 새 `refresh` 쿠키가 내려옵니다. "
                    + "로그아웃했거나 관리자가 권한 변경·차단·회원 삭제를 한 사용자는 refresh 토큰이 폐기되어 `400`이 오므로 다시 로그인해야 합니다.")
    @Parameter(name = "refresh", in = ParameterIn.COOKIE, required = true, description = "로그인·재발급 때 받은 리프레시 토큰",
            schema = @Schema(type = "string"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공 (본문 없음)",
                    content = @Content(schema = @Schema(hidden = true)), headers = {
                    @Header(name = "access", description = "새 액세스 토큰(JWT, 1시간)", schema = @Schema(type = "string")),
                    @Header(name = "Set-Cookie", description = "`refresh={새 리프레시 토큰}; Max-Age=86400; HttpOnly`",
                            schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "쿠키 없음·만료·위조 또는 이미 폐기된 리프레시 토큰", content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string",
                    allowableValues = {"No cookies found", "refresh token null", "refresh token expired", "invalid refresh token"}),
                    examples = @ExampleObject(value = "refresh token expired")))
    })
    ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);
}
