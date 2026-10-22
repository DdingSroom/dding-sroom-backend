package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.docs.AdminRoomApiDocs;
import com.example.ddingsroom.admin.service.AdminRoomService;
import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminRoomRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController implements AdminRoomApiDocs {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminRoomController.class);
    private final AdminRoomService adminRoomService;
    
    @Autowired
    public AdminRoomController(AdminRoomService adminRoomService) {
        this.adminRoomService = adminRoomService;
    }
    
    @Override
    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        logger.info("관리자 - 전체 스터디룸 목록 조회 요청");
        return adminRoomService.getAllRooms();
    }
    
    @Override
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomById(
            @PathVariable Long roomId) {
        logger.info("관리자 - 스터디룸 상세 조회 요청: roomId={}", roomId);
        return adminRoomService.getRoomById(roomId);
    }
    
    @Override
    @PostMapping
    public ResponseEntity<AdminResponseDTO> createRoom(@RequestBody AdminRoomRequestDTO roomRequestDTO) {
        logger.info("관리자 - 스터디룸 생성 요청: roomName={}", roomRequestDTO.getRoomName());
        return adminRoomService.createRoom(roomRequestDTO);
    }
    
    @Override
    @PutMapping("/{roomId}")
    public ResponseEntity<AdminResponseDTO> updateRoom(
            @PathVariable Long roomId,
            @RequestBody AdminRoomRequestDTO roomRequestDTO) {
        logger.info("관리자 - 스터디룸 수정 요청: roomId={}, roomName={}", roomId, roomRequestDTO.getRoomName());
        return adminRoomService.updateRoom(roomId, roomRequestDTO);
    }
    
    @Override
    @PutMapping("/{roomId}/status")
    public ResponseEntity<AdminResponseDTO> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam String status) {
        logger.info("관리자 - 스터디룸 상태 변경 요청: roomId={}, status={}", roomId, status);
        return adminRoomService.updateRoomStatus(roomId, status);
    }
    
    @Override
    @DeleteMapping("/{roomId}")
    public ResponseEntity<AdminResponseDTO> deleteRoom(
            @PathVariable Long roomId) {
        logger.info("관리자 - 스터디룸 삭제 요청: roomId={}", roomId);
        return adminRoomService.deleteRoom(roomId);
    }
    
    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> getRoomStatistics() {
        logger.info("관리자 - 스터디룸 통계 조회 요청");
        return adminRoomService.getRoomStatistics();
    }
    
    @Override
    @GetMapping("/{roomId}/reservations")
    public ResponseEntity<?> getRoomReservations(
            @PathVariable Long roomId) {
        logger.info("관리자 - 스터디룸별 예약 현황 조회 요청: roomId={}", roomId);
        return adminRoomService.getRoomReservations(roomId);
    }
} 