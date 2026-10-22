package com.example.ddingsroom.config;

import com.example.ddingsroom.user.dto.CustomUserDetails;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SecurityUtils securityUtils;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // 닉네임을 바꿔도(토큰의 username이 옛 값이어도) 토큰 id로 식별
    @Test
    void usesTokenIdWithoutNicknameLookup() {
        UserEntity tokenUser = new UserEntity();
        tokenUser.setId(15L);
        tokenUser.setUsername("old_nickname");
        tokenUser.setRole("ROLE_USER");
        CustomUserDetails principal = new CustomUserDetails(tokenUser);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        assertThat(securityUtils.getAuthenticatedUserId()).isEqualTo(15L);
        verifyNoInteractions(userRepository);
    }

    @Test
    void anonymousIsRejected() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertThatThrownBy(() -> securityUtils.getAuthenticatedUserId()).isInstanceOf(SecurityException.class);
    }

    @Test
    void missingAuthenticationIsRejected() {
        assertThatThrownBy(() -> securityUtils.getAuthenticatedUserId()).isInstanceOf(SecurityException.class);
    }

    @Test
    void otherPrincipalKeepsUsernameLookup() {
        User principal = new User("kim", "pw", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        UserEntity stored = new UserEntity();
        stored.setId(7L);
        when(userRepository.findByUsername("kim")).thenReturn(stored);

        assertThat(securityUtils.getAuthenticatedUserId()).isEqualTo(7L);
    }
}
