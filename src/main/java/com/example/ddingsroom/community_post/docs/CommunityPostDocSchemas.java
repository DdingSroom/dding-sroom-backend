package com.example.ddingsroom.community_post.docs;

import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// Map으로 내려가는 응답의 문서용 스키마 (실제 응답 객체로는 쓰지 않음)
public final class CommunityPostDocSchemas {

    private CommunityPostDocSchemas() {
    }

    @Schema(name = "CommunityPostSearchResponse", description = "조건부 게시글 조회 결과")
    public static class SearchResponse {
        @Schema(description = "게시글 목록 (없거나 오류면 빈 배열)", requiredMode = Schema.RequiredMode.REQUIRED)
        public List<CommunityPostResponseDTO> posts;
    }
}
