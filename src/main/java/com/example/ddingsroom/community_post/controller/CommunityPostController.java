package com.example.ddingsroom.community_post.controller;

import com.example.ddingsroom.community_post.docs.CommunityPostApiDocs;
import com.example.ddingsroom.community_post.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostRequestDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import com.example.ddingsroom.community_post.service.CommunityPostService;
import com.example.ddingsroom.config.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community-posts")
public class CommunityPostController implements CommunityPostApiDocs {

    private final CommunityPostService service;
    private final SecurityUtils securityUtils;

    @Autowired
    public CommunityPostController(CommunityPostService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    // 게시글 생성
    @Override
    @PostMapping
    public ResponseEntity<BaseResponseDTO<CommunityPostResponseDTO>> createCommunityPost(@RequestBody CommunityPostRequestDTO dto) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<CommunityPostResponseDTO> response = service.createCommunityPost(dto, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @Override
    @PutMapping
    public ResponseEntity<BaseResponseDTO<CommunityPostResponseDTO>> updateCommunityPost(@RequestBody CommunityPostRequestDTO dto) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<CommunityPostResponseDTO> response = service.updateCommunityPost(dto, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @Override
    @DeleteMapping
    public ResponseEntity<BaseResponseDTO<Void>> deleteCommunityPost(@RequestBody CommunityPostRequestDTO dto) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<Void> response = service.deleteCommunityPost(dto, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    // 조건부 조회 (기존 방식 유지)
    @Override
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> retrieveCommunityPost(
            @RequestParam(required = false) Long post_id,
            @RequestParam(required = false) Long user_id,
            @RequestParam(required = false) Integer category
    ) {
        List<CommunityPostResponseDTO> posts = service.retrieveCommunityPost(post_id, user_id, category);
        return ResponseEntity.ok(Map.of("posts", posts));
    }

    // 전체 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<CommunityPostResponseDTO>>> getAllCommunityPosts() {
        BaseResponseDTO<List<CommunityPostResponseDTO>> response = service.getAllCommunityPosts();
        return ResponseEntity.ok(response);
    }

    // 특정 게시글 조회
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<CommunityPostResponseDTO>> getCommunityPost(@PathVariable Long id) {
        BaseResponseDTO<CommunityPostResponseDTO> response = service.getCommunityPost(id);
        return ResponseEntity.ok(response);
    }

    // 내 게시글 조회 (토큰 기반, 선택적 카테고리 필터)
    @Override
    @GetMapping("/me")
    public ResponseEntity<BaseResponseDTO<List<CommunityPostResponseDTO>>> getMyPosts(@RequestParam(required = false) Integer category) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<List<CommunityPostResponseDTO>> response = service.getPostsByUserIdWithFilter(authenticatedUserId, category);
        return ResponseEntity.ok(response);
    }
    // CommunityPostController.java에 추가

    // 특정 사용자의 모든 게시글 조회
    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponseDTO<List<CommunityPostResponseDTO>>> getPostsByUserId(@PathVariable Long userId) {
        BaseResponseDTO<List<CommunityPostResponseDTO>> response = service.getPostsByUserId(userId);
        return ResponseEntity.ok(response);
    }

}
