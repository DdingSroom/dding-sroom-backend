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
public class SignUpDTO {
    @Schema(description = "이메일 (중복 불가, 서버는 형식만 검사하고 도메인은 제한하지 않음)", example = "student@mju.ac.kr",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "닉네임 (중복 불가)", example = "띵스룸", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "비밀번호 (6자 이상)", example = "password123", minLength = 6,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "나이 (선택)", example = "22", nullable = true)
    private String age;
    @Schema(description = "학번 (선택)", example = "60201234", nullable = true)
    private String studentNumber;
}
