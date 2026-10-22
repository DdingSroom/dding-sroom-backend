package com.example.ddingsroom.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationDTO {
    @Schema(description = "확인할 이메일 (로그인한 계정의 이메일과 비교)", example = "student@mju.ac.kr",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    public EmailVerificationDTO() {}

    public EmailVerificationDTO(String email) {
        this.email = email;
    }
}
