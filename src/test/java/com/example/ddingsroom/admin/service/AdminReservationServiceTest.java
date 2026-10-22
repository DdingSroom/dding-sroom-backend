package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReservationServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 25);

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AdminReservationService adminReservationService;

    // Long 캐시(-128~127) 밖의 사용자 ID도 값으로 비교
    @Test
    void reservationsByUserMatchLargeIdByValue() {
        when(userRepository.findById(300L)).thenReturn(Optional.of(user()));
        when(reservationRepository.findAll()).thenReturn(List.of(reservation()));

        ResponseEntity<?> response = adminReservationService.getReservationsByUserId(300L);

        assertThat(((Map<?, ?>) response.getBody()).get("totalCount")).isEqualTo(1);
    }

    // 자정(00:00) 시작 예약은 그날에 포함, 다음 날 00:00 시작은 제외
    @Test
    void reservationsByDateIncludeMidnightStart() {
        when(reservationRepository.findAll()).thenReturn(dayBoundaryReservations());

        ResponseEntity<?> response = adminReservationService.getReservationsByDate(DAY);

        assertThat(((Map<?, ?>) response.getBody()).get("totalCount")).isEqualTo(2);
    }

    @Test
    void reservationsByDateRangeIncludeMidnightStart() {
        when(reservationRepository.findAll()).thenReturn(dayBoundaryReservations());

        ResponseEntity<?> response = adminReservationService.getReservationsByDateRange(DAY, DAY.plusDays(1));

        assertThat(((Map<?, ?>) response.getBody()).get("totalCount")).isEqualTo(3);
    }

    @Test
    void dailyStatisticsIncludeMidnightStart() {
        when(reservationRepository.findAll()).thenReturn(dayBoundaryReservations());

        ResponseEntity<?> response = adminReservationService.getDailyReservationStatistics(DAY);

        Map<?, ?> statistics = (Map<?, ?>) ((AdminResponseDTO) response.getBody()).getData();
        assertThat(statistics.get("totalReservations")).isEqualTo(2L);
    }

    private List<ReservationEntity> dayBoundaryReservations() {
        return List.of(reservationAt(DAY.minusDays(1).atTime(23, 0)), reservationAt(DAY.atStartOfDay()),
                reservationAt(DAY.atTime(23, 0)), reservationAt(DAY.plusDays(1).atStartOfDay()));
    }

    private ReservationEntity reservationAt(LocalDateTime start) {
        ReservationEntity reservation = reservation();
        reservation.setStartTime(start);
        reservation.setEndTime(start.plusHours(1));
        return reservation;
    }

    private UserEntity user() {
        UserEntity user = new UserEntity();
        user.setId(300L);
        user.setUsername("kim");
        return user;
    }

    private ReservationEntity reservation() {
        RoomEntity room = new RoomEntity();
        room.setId(1L);
        room.setRoomName("1");
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setUser(user());
        reservation.setRoom(room);
        reservation.setStatus("RESERVED");
        return reservation;
    }
}
