package com.example.ddingsroom.support;

import com.example.ddingsroom.admin.service.AdminReservationService;
import com.example.ddingsroom.admin.service.AdminRoomService;
import com.example.ddingsroom.admin.service.AdminSystemService;
import com.example.ddingsroom.admin.service.AdminUserService;
import com.example.ddingsroom.community_post.service.CommunityPostService;
import com.example.ddingsroom.community_post_comment.service.CommunityPostCommentService;
import com.example.ddingsroom.config.SecurityUtils;
import com.example.ddingsroom.notification.service.NotificationService;
import com.example.ddingsroom.reservation.service.ReservationService;
import com.example.ddingsroom.suggest_post.service.SuggestPostSevice;
import com.example.ddingsroom.suggest_post_comment.service.SuggestPostCommentService;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import com.example.ddingsroom.user.jwt.JWTUtil;
import com.example.ddingsroom.user.repository.RefreshRepository;
import com.example.ddingsroom.user.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// 컨트롤러 전체를 서비스 mock으로 띄우는 슬라이스 테스트 (DB 미접속)
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:1/blocked",
        "spring.jpa.hibernate.ddl-auto=none"
})
public abstract class ControllerSliceTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected JoinService joinService;
    @MockitoBean
    protected JWTUtil jwtUtil;
    @MockitoBean
    protected RefreshRepository refreshRepository;
    @MockitoBean
    protected SecurityUtils securityUtils;
    @MockitoBean
    protected ReservationService reservationService;
    @MockitoBean
    protected CommunityPostService communityPostService;
    @MockitoBean
    protected CommunityPostCommentService communityPostCommentService;
    @MockitoBean
    protected SuggestPostSevice suggestPostSevice;
    @MockitoBean
    protected SuggestPostCommentService suggestPostCommentService;
    @MockitoBean
    protected SuggestPostImageService suggestPostImageService;
    @MockitoBean
    protected NotificationService notificationService;
    @MockitoBean
    protected AdminUserService adminUserService;
    @MockitoBean
    protected AdminReservationService adminReservationService;
    @MockitoBean
    protected AdminRoomService adminRoomService;
    @MockitoBean
    protected AdminSystemService adminSystemService;
}
