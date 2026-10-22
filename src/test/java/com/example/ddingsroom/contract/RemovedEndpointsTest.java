package com.example.ddingsroom.contract;

import com.example.ddingsroom.support.ControllerSliceTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 프론트 요청으로 삭제한 API (GET /api/community-posts/me 는 유지)
class RemovedEndpointsTest extends ControllerSliceTest {

    @ParameterizedTest
    @CsvSource({
            "GET, /user/test",
            "GET, /get-info-test",
            "POST, /user/join",
            "DELETE, /user/delete/a@mju.ac.kr",
            "GET, /user/mypage",
            "GET, /api/community-posts/user/15/category/1",
            "GET, /api/community-posts/user/15/posts",
            "GET, /api/community-posts/comments/me",
            "GET, /api/community-posts/comments/8",
            "GET, /api/community-posts/comments/user/15/comments",
            "GET, /api/community-posts/comments/user/15/replies",
            "GET, /api/community-posts/comments/user/15/post/8"
    })
    void removedEndpointReturns404(String method, String path) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(method), path).contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
    }
}
