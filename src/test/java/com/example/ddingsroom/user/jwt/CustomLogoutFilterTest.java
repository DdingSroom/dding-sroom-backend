package com.example.ddingsroom.user.jwt;

import com.example.ddingsroom.user.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CustomLogoutFilterTest {

    private final JWTUtil jwtUtil = mock(JWTUtil.class);
    private final RefreshRepository refreshRepository = mock(RefreshRepository.class);
    private final CustomLogoutFilter filter = new CustomLogoutFilter(jwtUtil, refreshRepository);
    private final FilterChain chain = mock(FilterChain.class);
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    void logoutWithoutAnyCookieIsBadRequest() throws Exception {
        filter.doFilter(new MockHttpServletRequest("POST", "/logout"), response, chain);

        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(chain, refreshRepository);
    }

    @Test
    void logoutWithoutRefreshCookieIsBadRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        request.setCookies(new Cookie("theme", "dark"));

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(chain, refreshRepository);
    }

    @Test
    void logoutWithRefreshCookieStillWorks() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        request.setCookies(new Cookie("refresh", "refresh-token"));
        when(jwtUtil.getCategory("refresh-token")).thenReturn("refresh");
        when(refreshRepository.existsByRefresh("refresh-token")).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getCookie("refresh").getMaxAge()).isZero();
        verify(refreshRepository).deleteByRefresh("refresh-token");
        verifyNoInteractions(chain);
    }

    @Test
    void otherRequestsPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/notification/list");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
