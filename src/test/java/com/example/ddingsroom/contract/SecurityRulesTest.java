package com.example.ddingsroom.contract;

import com.example.ddingsroom.admin.controller.AdminUserController;
import com.example.ddingsroom.admin.service.AdminUserService;
import com.example.ddingsroom.config.SecurityConfig;
import com.example.ddingsroom.config.SecurityUtils;
import com.example.ddingsroom.notification.controller.NotificationController;
import com.example.ddingsroom.notification.dto.BaseResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationReadResponseDTO;
import com.example.ddingsroom.notification.service.NotificationService;
import com.example.ddingsroom.reservation.controller.ReservationController;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import com.example.ddingsroom.reservation.service.ReservationService;
import com.example.ddingsroom.user.jwt.JWTUtil;
import com.example.ddingsroom.user.repository.RefreshRepository;
import com.example.ddingsroom.user.service.CustomUserDetailsService;
import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 실제 SecurityConfig(JWT 필터 포함)로 인가 규칙 검증
@WebMvcTest(controllers = {NotificationController.class, ReservationController.class, AdminUserController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:1/blocked",
        "spring.jpa.hibernate.ddl-auto=none"
})
class SecurityRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JWTUtil jwtUtil;
    @MockitoBean
    private RefreshRepository refreshRepository;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private SecurityUtils securityUtils;
    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private ReservationService reservationService;
    @MockitoBean
    private AdminUserService adminUserService;

    @BeforeEach
    void stubTokens() {
        stubToken("user-token", 10L, "kim", "ROLE_USER");
        stubToken("admin-token", 1L, "admin", "ROLE_ADMIN");
    }

    @Test
    void notificationReadRequiresLogin() throws Exception {
        mockMvc.perform(post("/api/notification/read").contentType(APPLICATION_JSON).content("{\"notificationId\":3}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void notificationReadWithToken() throws Exception {
        when(notificationService.markAsRead(3L))
                .thenReturn(new NotificationReadResponseDTO(3L, true, LocalDateTime.of(2026, 10, 22, 14, 3, 11)));

        mockMvc.perform(post("/api/notification/read").header("Authorization", "Bearer user-token")
                        .contentType(APPLICATION_JSON).content("{\"notificationId\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpointsStayPublic() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(BaseResponseDTO.success("공지사항 목록 조회 성공", List.of()));
        when(notificationService.getRecentNotificationsCount()).thenReturn(BaseResponseDTO.success("최근 3일 공지사항 개수 조회 성공", 0L));
        when(reservationService.getAllReservations()).thenReturn(new ReservationResponseDTO());

        mockMvc.perform(get("/api/notification/list")).andExpect(status().isOk());
        mockMvc.perform(get("/api/notification/recent-count")).andExpect(status().isOk());
        mockMvc.perform(get("/api/reservations/all-reservation")).andExpect(status().isOk());
    }

    @Test
    void protectedRulesUnchanged() throws Exception {
        mockMvc.perform(post("/api/reservations").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reservations/me")).andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/users").header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden());

        doReturn(ResponseEntity.ok(Map.of())).when(adminUserService).getAllUsers();
        mockMvc.perform(get("/admin/users").header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk());
    }

    // 오류 디스패치(/error)가 막히면 404·400·500이 본문 없는 403으로 바뀜
    @Test
    void errorPathIsPermittedAndReturnsCommonErrorBody() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/user/test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/user/test"))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    private void stubToken(String token, Long id, String username, String role) {
        when(jwtUtil.isExpired(token)).thenReturn(false);
        when(jwtUtil.getCategory(token)).thenReturn("access");
        when(jwtUtil.getUsername(token)).thenReturn(username);
        when(jwtUtil.getRole(token)).thenReturn(role);
        when(jwtUtil.getId(token)).thenReturn(id);
        when(jwtUtil.getEmail(token)).thenReturn(username + "@mju.ac.kr");
    }
}
