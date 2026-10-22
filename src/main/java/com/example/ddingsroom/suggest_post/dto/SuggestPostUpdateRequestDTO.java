package com.example.ddingsroom.suggest_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostUpdateRequestDTO {

    @Schema(description = "수정할 건의 ID", example = "10")
    @NotNull(message = "건의 ID는 필수입니다.")
    @JsonProperty("suggest_id")
    private Long suggestId;

    @Schema(description = "건의 제목", example = "보드마카가 다 떨어졌어요")
    @NotBlank(message = "건의 제목은 필수입니다.")
    @Size(max = 255, message = "건의 제목은 255자를 초과할 수 없습니다.")
    @JsonProperty("suggest_title")
    private String suggestTitle;

    @Schema(description = "건의 내용", example = "3번 방 보드마카를 채워주세요.")
    @NotBlank(message = "건의 내용은 필수입니다.")
    @JsonProperty("suggest_content")
    private String suggestContent;

    @Schema(description = "카테고리 이름", example = "기물 파손",
            allowableValues = {"미예약 사용자 신고", "분실물", "기물 파손", "시설 고장", "소음 공해", "기타"})
    @NotBlank(message = "카테고리는 필수입니다.")
    @JsonProperty("category")
    private String category;

    @Schema(description = "위치 이름", example = "스터디룸3",
            allowableValues = {"스터디룸1", "스터디룸2", "스터디룸3", "스터디룸4", "스터디룸5"})
    @NotBlank(message = "위치는 필수입니다.")
    @JsonProperty("location")
    private String location;

    @Schema(description = "답변 완료 여부 (기존 값을 그대로 보내면 유지)", example = "false")
    @NotNull(message = "답변 여부는 필수입니다.")
    @JsonProperty("is_answered")
    private Boolean isAnswered;

}
