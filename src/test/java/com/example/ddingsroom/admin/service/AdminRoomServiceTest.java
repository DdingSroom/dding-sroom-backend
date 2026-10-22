package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminRoomRequestDTO;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.reservation.repository.RoomRepository;
import com.example.ddingsroom.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRoomServiceTest {

    // Long 캐시(-128~127) 밖의 ID로 == 비교 버그가 없는지 함께 확인
    private static final long ROOM_ID = 200L;

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private AdminRoomService adminRoomService;

    @Test
    void roomWithActiveReservationIsKept() {
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room()));
        when(reservationRepository.existsByRoom_IdAndStatus(ROOM_ID, "RESERVED")).thenReturn(true);

        ResponseEntity<AdminResponseDTO> response = adminRoomService.deleteRoom(ROOM_ID);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("활성 예약이 있는 스터디룸은 삭제할 수 없습니다.");
        verify(roomRepository, never()).delete(any());
    }

    @Test
    void roomWithCancelledHistoryIsKept() {
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room()));
        when(reservationRepository.existsByRoom_Id(ROOM_ID)).thenReturn(true);

        ResponseEntity<AdminResponseDTO> response = adminRoomService.deleteRoom(ROOM_ID);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).startsWith("예약 이력이 있는 스터디룸은 삭제할 수 없습니다.");
        verify(roomRepository, never()).delete(any());
    }

    @Test
    void roomWithoutReservationsIsDeleted() {
        RoomEntity room = room();
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        ResponseEntity<AdminResponseDTO> response = adminRoomService.deleteRoom(ROOM_ID);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(roomRepository).delete(room);
    }

    @Test
    void updateKeepingSameNameIsNotDuplicate() {
        RoomEntity room = room();
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.findAll()).thenReturn(List.of(room()));

        AdminRoomRequestDTO request = new AdminRoomRequestDTO();
        request.setRoomName("A");
        request.setRoomStatus("IDLE");

        ResponseEntity<AdminResponseDTO> response = adminRoomService.updateRoom(ROOM_ID, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void roomReservationsMatchById() {
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room()));
        when(reservationRepository.findAll()).thenReturn(List.of(reservation()));

        ResponseEntity<?> response = adminRoomService.getRoomReservations(ROOM_ID);

        assertThat(((Map<?, ?>) response.getBody()).get("totalCount")).isEqualTo(1);
    }

    @Test
    void roomStatisticsCountById() {
        when(roomRepository.findAll()).thenReturn(List.of(room()));
        when(reservationRepository.findAll()).thenReturn(List.of(reservation()));

        ResponseEntity<?> response = adminRoomService.getRoomStatistics();

        assertThat(response.getBody().toString()).contains("A=1");
    }

    private RoomEntity room() {
        RoomEntity room = new RoomEntity();
        room.setId(ROOM_ID);
        room.setRoomName("A");
        room.setRoomStatus("IDLE");
        return room;
    }

    private ReservationEntity reservation() {
        UserEntity user = new UserEntity();
        user.setId(300L);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setRoom(room());
        reservation.setStatus("CANCELLED");
        return reservation;
    }
}
