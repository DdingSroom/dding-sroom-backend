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
public class CodeSendDTO {
    @Schema(description = "인증 코드를 받을 이메일", example = "student@mju.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
