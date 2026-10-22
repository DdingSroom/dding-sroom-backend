package com.example.ddingsroom.contract;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminUserDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import com.example.ddingsroom.community_post_comment.dto.CommunityPostCommentResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationReadResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationResponseDTO;
import com.example.ddingsroom.reservation.dto.BaseResponseDTO;
import com.example.ddingsroom.reservation.dto.ReservationRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostResponseDTO;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentResponseDTO;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageResponseDTO;
import com.example.ddingsroom.support.ControllerSliceTest;
import com.example.ddingsroom.user.dto.ChangeUsernameDTO;
import com.example.ddingsroom.user.dto.CustomUserDetails;
import com.example.ddingsroom.user.dto.ResponseDTO;
import com.example.ddingsroom.user.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 대표 API 응답 JSON이 바뀌지 않았는지 검증
class ResponseJsonContractTest extends ControllerSliceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 21, 21, 30);
    private static final LocalDateTime END = LocalDateTime.of(2026, 10, 21, 22, 30);
    private static final LocalDateTime CREATED = LocalDateTime.of(2026, 10, 21, 21, 5, 3, 293_429_000);
    private static final LocalDateTime UPDATED = LocalDateTime.of(2026, 10, 21, 21, 5, 26, 554_325_000);
    private static final LocalDate DATE = LocalDate.of(2026, 10, 21);

    // 응답 JSON 안의 날짜 표현
    private static final String START_JSON = "\"2026-10-21T21:30:00\"";
    private static final String END_JSON = "\"2026-10-21T22:30:00\"";
    private static final String CREATED_JSON = "\"2026-10-21T21:05:03\"";
    private static final String UPDATED_JSON = "\"2026-10-21T21:05:26\"";
    private static final String DATE_JSON = "\"2026-10-21\"";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allReservations() throws Exception {
        ReservationResponseDTO response = new ReservationResponseDTO();
        response.setMessage("데이터 조회가 성공적으로 진행되었습니다.");
        response.setReservations(List.of(reservation()));
        when(reservationService.getAllReservations()).thenReturn(response);

        mockMvc.perform(get("/api/reservations/all-reservation"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"데이터 조회가 성공적으로 진행되었습니다.","reservations":[$RESERVATION]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void createReservationParsesIsoRequestAndKeepsStatusRule() throws Exception {
        when(securityUtils.getAuthenticatedUserId()).thenReturn(10L);
        when(reservationService.createReservation(any(), eq(10L)))
                .thenReturn(new BaseResponseDTO("예약이 성공적으로 완료되었습니다."));

        mockMvc.perform(post("/api/reservations").contentType(APPLICATION_JSON).content("""
                        {"roomId":1,"reservationStartTime":"2026-10-21T21:30:00","reservationEndTime":"2026-10-21T22:30:00"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"message\":\"예약이 성공적으로 완료되었습니다.\"}", JsonCompareMode.STRICT));

        ArgumentCaptor<ReservationRequestDTO> captor = ArgumentCaptor.forClass(ReservationRequestDTO.class);
        verify(reservationService).createReservation(captor.capture(), eq(10L));
        assertThat(captor.getValue().getRoomId()).isEqualTo(1L);
        assertThat(captor.getValue().getReservationStartTime()).isEqualTo(START);
        assertThat(captor.getValue().getReservationEndTime()).isEqualTo(END);
    }

    @Test
    void createReservationStillAcceptsArrayDates() throws Exception {
        when(securityUtils.getAuthenticatedUserId()).thenReturn(10L);
        when(reservationService.createReservation(any(), eq(10L)))
                .thenReturn(new BaseResponseDTO("예약이 성공적으로 완료되었습니다."));

        mockMvc.perform(post("/api/reservations").contentType(APPLICATION_JSON).content("""
                        {"roomId":1,"reservationStartTime":[2026,10,21,21,30],"reservationEndTime":[2026,10,21,22,30]}
                        """))
                .andExpect(status().isCreated());

        ArgumentCaptor<ReservationRequestDTO> captor = ArgumentCaptor.forClass(ReservationRequestDTO.class);
        verify(reservationService).createReservation(captor.capture(), eq(10L));
        assertThat(captor.getValue().getReservationStartTime()).isEqualTo(START);
    }

    @Test
    void createReservationFailureStatusByMessage() throws Exception {
        when(securityUtils.getAuthenticatedUserId()).thenReturn(10L);
        when(reservationService.createReservation(any(), eq(10L)))
                .thenReturn(new BaseResponseDTO("동일한 시간대에 다른 스터디룸 예약이 불가능합니다."))
                .thenReturn(new BaseResponseDTO("과거 시간으로 예약할 수 없습니다."));
        String body = """
                {"roomId":1,"reservationStartTime":"2026-10-21T21:30:00","reservationEndTime":"2026-10-21T22:30:00"}
                """;

        mockMvc.perform(post("/api/reservations").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/reservations").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void cancelReservation() throws Exception {
        when(securityUtils.getAuthenticatedUserId()).thenReturn(10L);
        when(reservationService.cancelReservation(any(), eq(10L)))
                .thenReturn(new BaseResponseDTO("예약취소가 잘 되었습니다."))
                .thenReturn(new BaseResponseDTO("이용 중인 예약은 취소할 수 없습니다."))
                .thenReturn(new BaseResponseDTO("본인의 예약만 취소할 수 있습니다."));

        for (int expected : new int[]{200, 200, 400}) {
            mockMvc.perform(post("/api/reservations/cancel").contentType(APPLICATION_JSON)
                            .content("{\"reservationId\":26}"))
                    .andExpect(status().is(expected));
        }
    }

    @Test
    void communityPostList() throws Exception {
        when(communityPostService.getAllCommunityPosts()).thenReturn(
                com.example.ddingsroom.community_post.dto.BaseResponseDTO.success("게시글 목록 조회 성공", List.of(communityPost())));

        mockMvc.perform(get("/api/community-posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"게시글 목록 조회 성공","error":null,"data":[$POST]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void communityPostErrorIsStill200() throws Exception {
        when(communityPostService.getCommunityPost(99L)).thenReturn(
                com.example.ddingsroom.community_post.dto.BaseResponseDTO.error("해당 게시글을 찾을 수 없습니다. ID: 99"));

        mockMvc.perform(get("/api/community-posts/99"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"message":null,"error":"해당 게시글을 찾을 수 없습니다. ID: 99","data":null}
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void communityPostSearch() throws Exception {
        when(communityPostService.retrieveCommunityPost(null, 15L, null)).thenReturn(List.of(communityPost()));

        mockMvc.perform(get("/api/community-posts/search").param("user_id", "15"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("{\"posts\":[$POST]}"), JsonCompareMode.STRICT));
    }

    @Test
    void myCommunityPosts() throws Exception {
        when(securityUtils.getAuthenticatedUserId()).thenReturn(15L);
        when(communityPostService.getPostsByUserIdWithFilter(15L, null)).thenReturn(
                com.example.ddingsroom.community_post.dto.BaseResponseDTO.success("사용자 15의 전체 게시글 조회 성공 (총 1개)", List.of(communityPost())));

        mockMvc.perform(get("/api/community-posts/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"사용자 15의 전체 게시글 조회 성공 (총 1개)","error":null,"data":[$POST]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void commentsByPostHierarchy() throws Exception {
        CommunityPostCommentResponseDTO reply = comment(9L, 15L, "답글", 8L, "REPLY", 0L, null);
        CommunityPostCommentResponseDTO parent = comment(8L, 10L, "댓글", null, "COMMENT", 1L, List.of(reply));
        when(communityPostCommentService.getCommentsByPostId(8L)).thenReturn(
                com.example.ddingsroom.community_post_comment.dto.BaseResponseDTO.success("댓글 목록 조회 성공", List.of(parent)));

        mockMvc.perform(get("/api/community-posts/comments/post/8"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"댓글 목록 조회 성공","error":null,"data":[
                          {"id":8,"post_id":8,"user_id":10,"comment_content":"댓글","parent_comment_id":null,
                           "created_at":$CREATED,"updated_at":$UPDATED,"message":"성공",
                           "replies":[{"id":9,"post_id":8,"user_id":15,"comment_content":"답글","parent_comment_id":8,
                                       "created_at":$CREATED,"updated_at":$UPDATED,"message":"성공","replies":null,
                                       "comment_type":"REPLY","reply_count":0}],
                           "comment_type":"COMMENT","reply_count":1}]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void commentsByUser() throws Exception {
        when(communityPostCommentService.getCommentsByUserId(15L, "reply", null)).thenReturn(
                com.example.ddingsroom.community_post_comment.dto.BaseResponseDTO.success("사용자 15의 댓글 목록 조회 성공 (총 0개)", List.of()));

        mockMvc.perform(get("/api/community-posts/comments/user/15").param("type", "reply"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"message":"사용자 15의 댓글 목록 조회 성공 (총 0개)","error":null,"data":[]}
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void suggestionList() throws Exception {
        when(suggestPostSevice.retrieveSuggestPosts(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(List.of(suggestion()));

        mockMvc.perform(get("/api/suggestions"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"성공","suggestions":[{"id":10,"user_id":15,"suggest_title":"제목","suggest_content":"내용",
                          "created_at":$CREATED,"updated_at":$UPDATED,"category":"분실물","location":"스터디룸1","is_answered":false}]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void suggestionValidationErrorsReturnFieldMessages() throws Exception {
        mockMvc.perform(post("/api/suggestions").contentType(APPLICATION_JSON)
                        .content("{\"suggest_title\":\"\",\"suggest_content\":\"내용\",\"category\":\"기타\",\"location\":\"스터디룸1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"suggestTitle\":\"건의 제목은 필수입니다.\"}", JsonCompareMode.STRICT));
        mockMvc.perform(put("/api/suggestions").contentType(APPLICATION_JSON)
                        .content("{\"suggest_id\":10,\"suggest_title\":\"제목\",\"suggest_content\":\"내용\",\"category\":\"기타\",\"location\":\"스터디룸1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"isAnswered\":\"답변 여부는 필수입니다.\"}", JsonCompareMode.STRICT));
        verifyNoInteractions(suggestPostSevice);
    }

    @Test
    void suggestionListEmpty() throws Exception {
        when(suggestPostSevice.retrieveSuggestPosts(any(), any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/suggestions").param("user_id", "15"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"검색 조건에 해당하는 건의를 찾을 수 없습니다.\"}", JsonCompareMode.STRICT));
    }

    @Test
    void suggestionAnswers() throws Exception {
        when(suggestPostCommentService.getSuggestCommentsByPostId(8L)).thenReturn(
                List.of(new SuggestPostCommentResponseDTO(1L, 8L, 14L, "답변", CREATED, UPDATED)));

        mockMvc.perform(get("/api/suggestions/comments").param("suggest_post_id", "8"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"성공","comments":[{"id":1,"suggest_post_id":8,"user_id":14,"answer_content":"답변",
                          "created_at":$CREATED,"updated_at":$UPDATED}]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void suggestionImages() throws Exception {
        when(suggestPostImageService.getImagesBySuggestPostId(6L)).thenReturn(List.of(new SuggestPostImageResponseDTO(
                1L, 6L, "uuid-a.png", "https://bucket/suggest-images/uuid-a.png", "image/png", CREATED)));

        mockMvc.perform(get("/api/suggestions/images").param("suggest_post_id", "6"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"성공","images":[{"id":1,"suggest_post_id":6,"file_name":"uuid-a.png",
                          "file_url":"https://bucket/suggest-images/uuid-a.png","file_type":"image/png","uploaded_date":$CREATED}]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void notificationList() throws Exception {
        NotificationResponseDTO item = notification();
        item.setIsRead(false);
        when(notificationService.getAllNotifications()).thenReturn(
                com.example.ddingsroom.notification.dto.BaseResponseDTO.success("공지사항 목록 조회 성공", List.of(item)));

        mockMvc.perform(get("/api/notification/list"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"공지사항 목록 조회 성공","error":null,"data":[$NOTIFICATION_WITH_READ]}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void markNotificationAsRead() throws Exception {
        when(notificationService.markAsRead(3L)).thenReturn(new NotificationReadResponseDTO(3L, true, CREATED));

        mockMvc.perform(post("/api/notification/read").contentType(APPLICATION_JSON).content("{\"notificationId\":3}"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"공지사항을 읽음 처리했습니다.","error":null,
                         "data":{"notificationId":3,"isRead":true,"readAt":$CREATED}}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void markNotificationAsReadErrors() throws Exception {
        when(notificationService.markAsRead(99L)).thenThrow(new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        when(notificationService.markAsRead(null)).thenThrow(new IllegalArgumentException("공지사항 ID는 필수입니다."));
        when(notificationService.markAsRead(5L)).thenThrow(new AuthenticationCredentialsNotFoundException("로그인이 필요합니다."));

        mockMvc.perform(post("/api/notification/read").contentType(APPLICATION_JSON).content("{\"notificationId\":99}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {"message":null,"error":"공지사항을 찾을 수 없습니다.","data":null}
                        """, JsonCompareMode.STRICT));
        mockMvc.perform(post("/api/notification/read").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/notification/read").contentType(APPLICATION_JSON).content("{\"notificationId\":5}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notificationCreate() throws Exception {
        when(notificationService.createNotification(any())).thenReturn(
                com.example.ddingsroom.notification.dto.BaseResponseDTO.success("공지사항이 성공적으로 생성되었습니다!", notification()));

        mockMvc.perform(post("/api/notification/create").contentType(APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"내용\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"공지사항이 성공적으로 생성되었습니다!","error":null,"data":$NOTIFICATION}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void adminUsers() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "사용자 목록 조회 성공", "users", List.of(adminUser()), "totalCount", 1)))
                .when(adminUserService).getAllUsers();

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"사용자 목록 조회 성공","users":[$ADMIN_USER],"totalCount":1}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void adminUserDetail() throws Exception {
        doReturn(ResponseEntity.ok(AdminResponseDTO.success("사용자 조회 성공", adminUser())))
                .when(adminUserService).getUserById(10L);

        mockMvc.perform(get("/admin/users/10"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"사용자 조회 성공","success":true,"data":$ADMIN_USER}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void adminReservationsByDate() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("message", "2026-10-21 날짜의 예약 조회 성공", "date", DATE,
                "reservations", List.of(reservation()), "totalCount", 1)))
                .when(adminReservationService).getReservationsByDate(DATE);

        mockMvc.perform(get("/admin/reservations/date").param("date", "2026-10-21"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("""
                        {"message":"2026-10-21 날짜의 예약 조회 성공","date":$DATE,"reservations":[$RESERVATION],"totalCount":1}
                        """), JsonCompareMode.STRICT));
    }

    @Test
    void userCodeSend() throws Exception {
        when(joinService.autentication1(any())).thenReturn(
                ResponseEntity.ok(new ResponseDTO("인증 코드가 a@mju.ac.kr로 전송되었습니다.")));

        mockMvc.perform(post("/user/code-send").contentType(APPLICATION_JSON).content("{\"email\":\"a@mju.ac.kr\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json;charset=utf8"))
                .andExpect(content().json("{\"message\":\"인증 코드가 a@mju.ac.kr로 전송되었습니다.\"}", JsonCompareMode.STRICT));
    }

    @Test
    void changeUsernameUsesTokenUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setEmail("a@mju.ac.kr");
        user.setUsername("kim");
        user.setRole("ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), null, List.of()));
        when(joinService.changeUsername(eq(10L), any()))
                .thenReturn(ResponseEntity.ok(new ResponseDTO("사용자명이 성공적으로 변경되었습니다.")));

        mockMvc.perform(put("/user/change-username").contentType(APPLICATION_JSON).content("{\"newUsername\":\"lee\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"사용자명이 성공적으로 변경되었습니다.\"}", JsonCompareMode.STRICT));

        ArgumentCaptor<ChangeUsernameDTO> captor = ArgumentCaptor.forClass(ChangeUsernameDTO.class);
        verify(joinService).changeUsername(eq(10L), captor.capture());
        assertThat(captor.getValue().getNewUsername()).isEqualTo("lee");
    }

    @Test
    void reissueWithoutCookie() throws Exception {
        mockMvc.perform(post("/reissue"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No cookies found"));
    }

    private static String json(String template) {
        return template
                .replace("$RESERVATION", """
                        {"id":26,"userId":10,"roomId":1,"roomName":"1","startTime":$START,"endTime":$END,
                         "status":"CANCELLED","createdAt":$CREATED,"updatedAt":$UPDATED}""")
                .replace("$POST", """
                        {"id":15,"user_id":15,"title":"제목","content":"내용","category":1,
                         "created_at":$CREATED,"updated_at":$UPDATED,"message":"성공"}""")
                .replace("$NOTIFICATION_WITH_READ", """
                        {"id":3,"userId":14,"title":"제목","content":"내용","createdAt":$CREATED,"updatedAt":$UPDATED,
                         "viewCount":3,"message":"성공","isRead":false}""")
                .replace("$NOTIFICATION", """
                        {"id":3,"userId":14,"title":"제목","content":"내용","createdAt":$CREATED,"updatedAt":$UPDATED,
                         "viewCount":3,"message":"성공"}""")
                .replace("$ADMIN_USER", """
                        {"id":10,"email":"a@mju.ac.kr","username":"kim","age":null,"studentNumber":null,
                         "role":"ROLE_USER","state":"normal","registrationDate":$CREATED}""")
                .replace("$START", START_JSON)
                .replace("$END", END_JSON)
                .replace("$CREATED", CREATED_JSON)
                .replace("$UPDATED", UPDATED_JSON)
                .replace("$DATE", DATE_JSON);
    }

    private static ReservationResponseDTO.ReservationDTO reservation() {
        ReservationResponseDTO.ReservationDTO dto = new ReservationResponseDTO.ReservationDTO();
        dto.setId(26L);
        dto.setUserId(10L);
        dto.setRoomId(1L);
        dto.setRoomName("1");
        dto.setStartTime(START);
        dto.setEndTime(END);
        dto.setStatus("CANCELLED");
        dto.setCreatedAt(CREATED);
        dto.setUpdatedAt(UPDATED);
        return dto;
    }

    private static CommunityPostResponseDTO communityPost() {
        CommunityPostResponseDTO dto = new CommunityPostResponseDTO();
        dto.setId(15L);
        dto.setUserId(15L);
        dto.setTitle("제목");
        dto.setContent("내용");
        dto.setCategory(1);
        dto.setCreatedAt(CREATED);
        dto.setUpdatedAt(UPDATED);
        dto.setMessage("성공");
        return dto;
    }

    private static CommunityPostCommentResponseDTO comment(Long id, Long userId, String content, Long parentId,
                                                           String type, Long replyCount,
                                                           List<CommunityPostCommentResponseDTO> replies) {
        CommunityPostCommentResponseDTO dto = new CommunityPostCommentResponseDTO();
        dto.setId(id);
        dto.setPostId(8L);
        dto.setUserId(userId);
        dto.setCommentContent(content);
        dto.setParentCommentId(parentId);
        dto.setCreatedAt(CREATED);
        dto.setUpdatedAt(UPDATED);
        dto.setMessage("성공");
        dto.setReplies(replies);
        dto.setCommentType(type);
        dto.setReplyCount(replyCount);
        return dto;
    }

    private static SuggestPostResponseDTO suggestion() {
        UserEntity user = new UserEntity();
        user.setId(15L);
        SuggestPostEntity entity = new SuggestPostEntity(user, "제목", "내용", 1, 1);
        ReflectionTestUtils.setField(entity, "id", 10L);
        ReflectionTestUtils.setField(entity, "createdAt", CREATED);
        ReflectionTestUtils.setField(entity, "updatedAt", UPDATED);
        return new SuggestPostResponseDTO(entity);
    }

    private static NotificationResponseDTO notification() {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(3L);
        dto.setUserId(14L);
        dto.setTitle("제목");
        dto.setContent("내용");
        dto.setCreatedAt(CREATED);
        dto.setUpdatedAt(UPDATED);
        dto.setViewCount(3);
        dto.setMessage("성공");
        return dto;
    }

    private static AdminUserDTO adminUser() {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setEmail("a@mju.ac.kr");
        user.setUsername("kim");
        user.setRole("ROLE_USER");
        user.setState("normal");
        user.setRegistrationDate(CREATED);
        return AdminUserDTO.fromEntity(user);
    }
}
