package com.example.ddingsroom.admin.docs;

import com.example.ddingsroom.admin.dto.AdminUserDTO;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

// 관리자 API의 Map 응답을 설명하는 문서용 스키마 (실제 응답 객체로는 쓰지 않음)
public final class AdminDocSchemas {

    private AdminDocSchemas() {
    }

    public abstract static class AdminResult {
        @Schema(description = "처리 결과 메시지", example = "조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(description = "성공 여부", example = "true", requiredMode = REQUIRED)
        public boolean success;
    }

    @Schema(name = "AdminRoom", description = "스터디룸")
    public static class Room {
        @Schema(description = "스터디룸 ID", example = "1", requiredMode = REQUIRED)
        public Long id;
        @Schema(description = "스터디룸 이름", example = "1", requiredMode = REQUIRED)
        public String roomName;
        @Schema(description = "상태", example = "IDLE", allowableValues = {"IDLE", "OCCUPIED", "MAINTENANCE"},
                requiredMode = REQUIRED)
        public String roomStatus;
    }

    @Schema(name = "AdminUserListResponse", description = "전체 사용자 목록")
    public static class UserListResponse {
        @Schema(description = "결과 메시지", example = "사용자 목록 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(requiredMode = REQUIRED)
        public List<AdminUserDTO> users;
        @Schema(description = "사용자 수", example = "12", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminUserDetailResponse", description = "사용자 상세")
    public static class UserDetailResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public AdminUserDTO data;
    }

    @Schema(name = "AdminUserStatistics", description = "사용자 통계")
    public static class UserStatistics {
        @Schema(description = "전체 사용자 수", example = "12", requiredMode = REQUIRED)
        public long totalUsers;
        @Schema(description = "관리자 수", example = "8", requiredMode = REQUIRED)
        public long adminCount;
        @Schema(description = "일반 사용자 수", example = "4", requiredMode = REQUIRED)
        public long normalUserCount;
        @Schema(description = "상태 normal 수", example = "12", requiredMode = REQUIRED)
        public long activeUsers;
        @Schema(description = "상태 blocked 수", example = "0", requiredMode = REQUIRED)
        public long blockedUsers;
        @Schema(description = "최근 7일 가입자 수", example = "1", requiredMode = REQUIRED)
        public long recentUsers;
    }

    @Schema(name = "AdminUserStatisticsResponse", description = "사용자 통계 응답")
    public static class UserStatisticsResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public UserStatistics data;
    }

    @Schema(name = "AdminReservationListResponse", description = "전체 예약 목록")
    public static class ReservationListResponse {
        @Schema(description = "결과 메시지", example = "전체 예약 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(requiredMode = REQUIRED)
        public List<ReservationResponseDTO.ReservationDTO> reservations;
        @Schema(description = "예약 수", example = "21", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminReservationDetailResponse", description = "예약 상세")
    public static class ReservationDetailResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public ReservationResponseDTO.ReservationDTO data;
    }

    @Schema(name = "AdminReservationsByDateResponse", description = "날짜별 예약 (시작 일시 기준)")
    public static class ReservationsByDateResponse {
        @Schema(description = "결과 메시지", example = "2026-10-21 날짜의 예약 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(description = "조회한 날짜", example = "2026-10-21", requiredMode = REQUIRED)
        public LocalDate date;
        @Schema(requiredMode = REQUIRED)
        public List<ReservationResponseDTO.ReservationDTO> reservations;
        @Schema(description = "예약 수", example = "3", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminReservationsByDateRangeResponse", description = "기간별 예약 (시작 일시 기준)")
    public static class ReservationsByDateRangeResponse {
        @Schema(description = "결과 메시지", example = "2026-10-01 ~ 2026-10-31 기간의 예약 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(description = "시작 날짜", example = "2026-10-01", requiredMode = REQUIRED)
        public LocalDate startDate;
        @Schema(description = "종료 날짜", example = "2026-10-31", requiredMode = REQUIRED)
        public LocalDate endDate;
        @Schema(requiredMode = REQUIRED)
        public List<ReservationResponseDTO.ReservationDTO> reservations;
        @Schema(description = "예약 수", example = "10", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminReservationStatistics", description = "예약 통계")
    public static class ReservationStatistics {
        @Schema(description = "전체 예약 수", example = "21", requiredMode = REQUIRED)
        public long totalReservations;
        @Schema(description = "RESERVED 수", example = "12", requiredMode = REQUIRED)
        public long activeReservations;
        @Schema(description = "CANCELLED 수", example = "9", requiredMode = REQUIRED)
        public long cancelledReservations;
        @Schema(description = "오늘 생성된 예약 수", example = "2", requiredMode = REQUIRED)
        public long todayReservations;
        @Schema(description = "최근 7일 생성된 예약 수", example = "5", requiredMode = REQUIRED)
        public long weeklyReservations;
    }

    @Schema(name = "AdminReservationStatisticsResponse", description = "예약 통계 응답")
    public static class ReservationStatisticsResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public ReservationStatistics data;
    }

    @Schema(name = "AdminDailyReservationStatistics", description = "일별 예약 통계 (시작 일시 기준)")
    public static class DailyReservationStatistics {
        @Schema(description = "날짜", example = "2026-10-21", requiredMode = REQUIRED)
        public LocalDate date;
        @Schema(description = "예약 수", example = "4", requiredMode = REQUIRED)
        public long totalReservations;
        @Schema(description = "RESERVED 수", example = "3", requiredMode = REQUIRED)
        public long activeReservations;
        @Schema(description = "CANCELLED 수", example = "1", requiredMode = REQUIRED)
        public long cancelledReservations;
        @Schema(description = "시작 시각별 예약 수 (키: \"09:00\" ~ \"22:00\")", example = "{\"09:00\": 0, \"10:00\": 2}",
                requiredMode = REQUIRED)
        public Map<String, Long> hourlyStatistics;
    }

    @Schema(name = "AdminDailyReservationStatisticsResponse", description = "일별 예약 통계 응답")
    public static class DailyReservationStatisticsResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public DailyReservationStatistics data;
    }

    @Schema(name = "AdminUserReservationsResponse", description = "특정 사용자의 예약 목록")
    public static class UserReservationsResponse {
        @Schema(description = "결과 메시지", example = "사용자 'kim'의 예약 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(description = "사용자 닉네임", example = "kim", requiredMode = REQUIRED)
        public String user;
        @Schema(description = "사용자 ID", example = "10", requiredMode = REQUIRED)
        public Long userId;
        @Schema(requiredMode = REQUIRED)
        public List<ReservationResponseDTO.ReservationDTO> reservations;
        @Schema(description = "예약 수", example = "3", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminRoomListResponse", description = "전체 스터디룸 목록")
    public static class RoomListResponse {
        @Schema(description = "결과 메시지", example = "스터디룸 목록 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(requiredMode = REQUIRED)
        public List<Room> rooms;
        @Schema(description = "스터디룸 수", example = "5", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminRoomDetailResponse", description = "스터디룸 상세")
    public static class RoomDetailResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public Room data;
    }

    @Schema(name = "AdminRoomStatistics", description = "스터디룸 통계")
    public static class RoomStatistics {
        @Schema(description = "전체 스터디룸 수", example = "5", requiredMode = REQUIRED)
        public long totalRooms;
        @Schema(description = "IDLE 수", example = "5", requiredMode = REQUIRED)
        public long idleRooms;
        @Schema(description = "OCCUPIED 수", example = "0", requiredMode = REQUIRED)
        public long occupiedRooms;
        @Schema(description = "MAINTENANCE 수", example = "0", requiredMode = REQUIRED)
        public long maintenanceRooms;
        @Schema(description = "스터디룸 이름별 전체 예약 수", example = "{\"1\": 18, \"2\": 3}", requiredMode = REQUIRED)
        public Map<String, Long> roomReservationCounts;
    }

    @Schema(name = "AdminRoomStatisticsResponse", description = "스터디룸 통계 응답")
    public static class RoomStatisticsResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public RoomStatistics data;
    }

    @Schema(name = "AdminRoomReservationsResponse", description = "특정 스터디룸의 예약 목록")
    public static class RoomReservationsResponse {
        @Schema(description = "결과 메시지", example = "스터디룸 '1'의 예약 현황 조회 성공", requiredMode = REQUIRED)
        public String message;
        @Schema(requiredMode = REQUIRED)
        public Room room;
        @Schema(requiredMode = REQUIRED)
        public List<ReservationResponseDTO.ReservationDTO> reservations;
        @Schema(description = "예약 수", example = "18", requiredMode = REQUIRED)
        public int totalCount;
    }

    @Schema(name = "AdminSystemStatus", description = "시스템 상태")
    public static class SystemStatus {
        @Schema(example = "healthy", requiredMode = REQUIRED)
        public String database;
        @Schema(example = "running", requiredMode = REQUIRED)
        public String server;
        @Schema(description = "조회 시각", example = "2026-10-22T14:03:11", requiredMode = REQUIRED)
        public LocalDateTime lastUpdate;
    }

    @Schema(name = "AdminDashboard", description = "대시보드")
    public static class Dashboard {
        @Schema(example = "12", requiredMode = REQUIRED)
        public long totalUsers;
        @Schema(example = "21", requiredMode = REQUIRED)
        public long totalReservations;
        @Schema(example = "5", requiredMode = REQUIRED)
        public long totalRooms;
        @Schema(description = "오늘 생성된 예약 수", example = "2", requiredMode = REQUIRED)
        public long todayReservations;
        @Schema(description = "RESERVED 수", example = "12", requiredMode = REQUIRED)
        public long activeReservations;
        @Schema(requiredMode = REQUIRED)
        public SystemStatus systemStatus;
    }

    @Schema(name = "AdminDashboardResponse", description = "대시보드 응답")
    public static class DashboardResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public Dashboard data;
    }

    @Schema(name = "AdminSystemUserStats", description = "사용자 집계")
    public static class SystemUserStats {
        @Schema(example = "12", requiredMode = REQUIRED)
        public long total;
        @Schema(example = "8", requiredMode = REQUIRED)
        public long admins;
        @Schema(example = "4", requiredMode = REQUIRED)
        public long users;
        @Schema(example = "12", requiredMode = REQUIRED)
        public long active;
        @Schema(example = "0", requiredMode = REQUIRED)
        public long blocked;
    }

    @Schema(name = "AdminSystemReservationStats", description = "예약 집계")
    public static class SystemReservationStats {
        @Schema(example = "21", requiredMode = REQUIRED)
        public long total;
        @Schema(example = "12", requiredMode = REQUIRED)
        public long active;
        @Schema(example = "9", requiredMode = REQUIRED)
        public long cancelled;
    }

    @Schema(name = "AdminSystemRoomStats", description = "스터디룸 집계")
    public static class SystemRoomStats {
        @Schema(example = "5", requiredMode = REQUIRED)
        public long total;
        @Schema(example = "5", requiredMode = REQUIRED)
        public long idle;
        @Schema(example = "0", requiredMode = REQUIRED)
        public long occupied;
        @Schema(example = "0", requiredMode = REQUIRED)
        public long maintenance;
    }

    @Schema(name = "AdminSystemStatistics", description = "전체 시스템 통계")
    public static class SystemStatistics {
        @Schema(requiredMode = REQUIRED)
        public SystemUserStats users;
        @Schema(requiredMode = REQUIRED)
        public SystemReservationStats reservations;
        @Schema(requiredMode = REQUIRED)
        public SystemRoomStats rooms;
        @Schema(description = "생성 시각", example = "2026-10-22T14:03:11", requiredMode = REQUIRED)
        public LocalDateTime generatedAt;
    }

    @Schema(name = "AdminSystemStatisticsResponse", description = "전체 시스템 통계 응답")
    public static class SystemStatisticsResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public SystemStatistics data;
    }

    @Schema(name = "AdminMemoryInfo", description = "JVM 메모리 (bytes)")
    public static class MemoryInfo {
        @Schema(example = "268435456", requiredMode = REQUIRED)
        public long total;
        @Schema(example = "134217728", requiredMode = REQUIRED)
        public long used;
        @Schema(example = "134217728", requiredMode = REQUIRED)
        public long free;
        @Schema(example = "50.0", requiredMode = REQUIRED)
        public double usagePercentage;
    }

    @Schema(name = "AdminSystemHealth", description = "시스템 상태")
    public static class SystemHealth {
        @Schema(example = "healthy", allowableValues = {"healthy", "error"}, requiredMode = REQUIRED)
        public String database;
        @Schema(requiredMode = REQUIRED)
        public MemoryInfo memory;
        @Schema(example = "running", requiredMode = REQUIRED)
        public String server;
        @Schema(description = "조회 시점의 현재 시각(epoch ms)", example = "1792641791000", requiredMode = REQUIRED)
        public long uptime;
        @Schema(description = "조회 시각", example = "2026-10-22T14:03:11", requiredMode = REQUIRED)
        public LocalDateTime checkTime;
    }

    @Schema(name = "AdminSystemHealthResponse", description = "시스템 상태 응답")
    public static class SystemHealthResponse extends AdminResult {
        @Schema(requiredMode = REQUIRED)
        public SystemHealth data;
    }
}
