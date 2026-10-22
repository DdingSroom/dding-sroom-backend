package com.example.ddingsroom.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPasswordDTO {
    @Schema(description = "가입한 이메일", example = "student@mju.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "새 비밀번호 (6자 이상)", example = "newPassword123", minLength = 6,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
