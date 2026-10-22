package com.example.ddingsroom.user.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeVerifyDTO {
    @Schema(description = "인증 코드를 받은 이메일", example = "student@mju.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "메일로 받은 6자리 코드 (발송 후 5분 유효)", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
