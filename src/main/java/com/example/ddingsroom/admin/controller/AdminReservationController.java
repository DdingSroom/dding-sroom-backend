package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.docs.AdminReservationApiDocs;
import com.example.ddingsroom.admin.service.AdminReservationService;
import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController implements AdminReservationApiDocs {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminReservationController.class);
    private final AdminReservationService adminReservationService;
    
    @Autowired
    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }
    
    @Override
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        logger.info("관리자 - 전체 예약 목록 조회 요청");
        return adminReservationService.getAllReservations();
    }
    
    @Override
    @GetMapping("/{reservationId}")
    public ResponseEntity<?> getReservationById(
            @PathVariable Long reservationId) {
        logger.info("관리자 - 예약 상세 조회 요청: reservationId={}", reservationId);
        return adminReservationService.getReservationById(reservationId);
    }
    
    @Override
    @PostMapping("/{reservationId}/force-cancel")
    public ResponseEntity<AdminResponseDTO> forceCancelReservation(
            @PathVariable Long reservationId) {
        logger.info("관리자 - 예약 강제 취소 요청: reservationId={}", reservationId);
        return adminReservationService.forceCancelReservation(reservationId);
    }
    
    @Override
    @GetMapping("/date")
    public ResponseEntity<?> getReservationsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("관리자 - 날짜별 예약 조회 요청: date={}", date);
        return adminReservationService.getReservationsByDate(date);
    }
    
    @Override
    @GetMapping("/date-range")
    public ResponseEntity<?> getReservationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("관리자 - 기간별 예약 조회 요청: startDate={}, endDate={}", startDate, endDate);
        return adminReservationService.getReservationsByDateRange(startDate, endDate);
    }
    
    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> getReservationStatistics() {
        logger.info("관리자 - 예약 통계 조회 요청");
        return adminReservationService.getReservationStatistics();
    }
    
    @Override
    @GetMapping("/statistics/daily")
    public ResponseEntity<?> getDailyReservationStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("관리자 - 일별 예약 통계 조회 요청: date={}", date);
        return adminReservationService.getDailyReservationStatistics(date);
    }
    
    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUserId(
            @PathVariable Long userId) {
        logger.info("관리자 - 사용자별 예약 조회 요청: userId={}", userId);
        return adminReservationService.getReservationsByUserId(userId);
    }
} 