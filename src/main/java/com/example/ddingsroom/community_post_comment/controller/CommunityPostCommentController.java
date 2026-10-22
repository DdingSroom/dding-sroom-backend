package com.example.ddingsroom.community_post_comment.controller;

import com.example.ddingsroom.community_post_comment.docs.CommunityCommentApiDocs;
import com.example.ddingsroom.community_post_comment.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post_comment.dto.CommunityPostCommentRequestDTO;
import com.example.ddingsroom.community_post_comment.dto.CommunityPostCommentResponseDTO;
import com.example.ddingsroom.community_post_comment.service.CommunityPostCommentService;
import com.example.ddingsroom.config.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community-posts/comments")
public class CommunityPostCommentController implements CommunityCommentApiDocs {

    private final CommunityPostCommentService service;
    private final SecurityUtils securityUtils;

    @Autowired
    public CommunityPostCommentController(CommunityPostCommentService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    // 기존 메서드들...
    @Override
    @PostMapping
    public ResponseEntity<BaseResponseDTO<CommunityPostCommentResponseDTO>> createComment(@RequestBody CommunityPostCommentRequestDTO dto) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<CommunityPostCommentResponseDTO> response = service.createComment(dto, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping
    public ResponseEntity<BaseResponseDTO<CommunityPostCommentResponseDTO>> updateComment(@RequestBody CommunityPostCommentRequestDTO dto) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<CommunityPostCommentResponseDTO> response = service.updateComment(dto, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<BaseResponseDTO<Void>> deleteComment(@RequestBody CommunityPostCommentRequestDTO dto) {
        Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
        BaseResponseDTO<Void> response = service.deleteComment(dto, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/post/{postId}")
    public ResponseEntity<BaseResponseDTO<List<CommunityPostCommentResponseDTO>>> getCommentsByPostId(@PathVariable Long postId) {
        BaseResponseDTO<List<CommunityPostCommentResponseDTO>> response = service.getCommentsByPostId(postId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<BaseResponseDTO<List<CommunityPostCommentResponseDTO>>> getRepliesByCommentId(@PathVariable Long commentId) {
        BaseResponseDTO<List<CommunityPostCommentResponseDTO>> response = service.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(response);
    }

    // ===== 새로 추가할 유저 관련 엔드포인트들 =====

    // 특정 사용자의 모든 댓글 조회 (댓글 + 대댓글)
    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponseDTO<List<CommunityPostCommentResponseDTO>>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String type,           // "comment", "reply"
            @RequestParam(required = false) Long postId           // 특정 게시글 필터
    ) {
        BaseResponseDTO<List<CommunityPostCommentResponseDTO>> response = service.getCommentsByUserId(userId, type, postId);
        return ResponseEntity.ok(response);
    }
}
