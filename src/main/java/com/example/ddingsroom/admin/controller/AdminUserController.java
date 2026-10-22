package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.docs.AdminUserApiDocs;
import com.example.ddingsroom.admin.service.AdminUserService;
import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController implements AdminUserApiDocs {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);
    private final AdminUserService adminUserService;
    
    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
    
    @Override
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logger.info("관리자 - 전체 사용자 목록 조회 요청");
        return adminUserService.getAllUsers();
    }
    
    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long userId) {
        logger.info("관리자 - 사용자 상세 조회 요청: userId={}", userId);
        return adminUserService.getUserById(userId);
    }
    
    @Override
    @PutMapping("/{userId}/status")
    public ResponseEntity<AdminResponseDTO> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam String status) {
        logger.info("관리자 - 사용자 상태 변경 요청: userId={}, status={}", userId, status);
        return adminUserService.updateUserStatus(userId, status);
    }
    
    @Override
    @PutMapping("/{userId}/role")
    public ResponseEntity<AdminResponseDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        logger.info("관리자 - 사용자 권한 변경 요청: userId={}, role={}", userId, role);
        return adminUserService.updateUserRole(userId, role);
    }
    
    @Override
    @DeleteMapping("/{userId}")
    public ResponseEntity<AdminResponseDTO> deleteUser(
            @PathVariable Long userId) {
        logger.info("관리자 - 사용자 삭제 요청: userId={}", userId);
        return adminUserService.deleteUser(userId);
    }
    
    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        logger.info("관리자 - 사용자 통계 조회 요청");
        return adminUserService.getUserStatistics();
    }
} 