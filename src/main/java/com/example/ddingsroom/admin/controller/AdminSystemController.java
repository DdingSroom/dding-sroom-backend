package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.docs.AdminSystemApiDocs;
import com.example.ddingsroom.admin.service.AdminSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSystemController implements AdminSystemApiDocs {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminSystemController.class);
    private final AdminSystemService adminSystemService;
    
    @Autowired
    public AdminSystemController(AdminSystemService adminSystemService) {
        this.adminSystemService = adminSystemService;
    }
    
    @Override
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        logger.info("관리자 - 대시보드 조회 요청");
        return adminSystemService.getAdminDashboard();
    }
    
    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> getSystemStatistics() {
        logger.info("관리자 - 전체 시스템 통계 조회 요청");
        return adminSystemService.getSystemStatistics();
    }
    
    @Override
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth() {
        logger.info("관리자 - 시스템 상태 확인 요청");
        return adminSystemService.getSystemHealth();
    }
} 