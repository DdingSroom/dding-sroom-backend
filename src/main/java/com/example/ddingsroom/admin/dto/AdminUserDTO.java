package com.example.ddingsroom.admin.dto;

import com.example.ddingsroom.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "사용자 정보 (관리자용)")
public class AdminUserDTO {
    @Schema(description = "사용자 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "이메일", example = "student@mju.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String email;
    @Schema(description = "닉네임", example = "띵스룸", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "나이", example = "22", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String age;
    @Schema(description = "학번", example = "60201234", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String studentNumber;
    @Schema(description = "권한", example = "ROLE_USER", allowableValues = {"ROLE_USER", "ROLE_ADMIN"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;
    @Schema(description = "계정 상태", example = "normal", allowableValues = {"normal", "blocked"},
            requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String state;
    @Schema(description = "가입 일시", example = "2026-06-14T20:16:33", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime registrationDate;
    
    public AdminUserDTO() {}
    
    public AdminUserDTO(UserEntity userEntity) {
        if (userEntity != null) {
            this.id = userEntity.getId();
            this.email = userEntity.getEmail();
            this.username = userEntity.getUsername();
            this.age = userEntity.getAge();
            this.studentNumber = userEntity.getStudentNumber();
            this.role = userEntity.getRole();
            this.state = userEntity.getState();
            this.registrationDate = userEntity.getRegistrationDate();
        }
    }
    
    public static AdminUserDTO fromEntity(UserEntity userEntity) {
        return new AdminUserDTO(userEntity);
    }
} 