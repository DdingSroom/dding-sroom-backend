package com.example.ddingsroom.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeUsernameDTO {
    // userId는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.
    @Schema(description = "새 닉네임 (중복 불가)", example = "띵스룸", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newUsername;

    public ChangeUsernameDTO() {}

    public ChangeUsernameDTO(String newUsername) {
        this.newUsername = newUsername;
    }

}
