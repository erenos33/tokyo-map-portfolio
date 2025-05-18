package me.tokyomap.service.impl;

import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.domain.user.role.UserRole;
import me.tokyomap.dto.user.LoginRequestDto;
import me.tokyomap.dto.LoginResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User verifiedUser;

    @BeforeEach
    void setUp() {
        verifiedUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .emailVerified(true)
                .role(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("ログイン_成功")
    void ログイン_成功() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(verifiedUser));
        given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);
        given(jwtTokenProvider.createToken("test@example.com", "USER")).willReturn("mocked-token");
        given(jwtTokenProvider.getExpirationDate()).willReturn(new Date(9999999999L));

        LoginResponseDto response = authService.login(request.getEmail(), request.getPassword());

        assertThat(response.getAccessToken()).isEqualTo("mocked-token");
        assertThat(response.getExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("ログイン_失敗_ユーザーなし")
    void ログイン_失敗_ユーザーなし() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("nouser@example.com");
        request.setPassword("password");

        given(userRepository.findByEmail("nouser@example.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request.getEmail(), request.getPassword()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("ログイン_失敗_未認証ユーザー")
    void ログイン_失敗_未認証ユーザー() {
        User notVerifiedUser = User.builder()
                .email("unverified@example.com")
                .password("encodedPassword")
                .emailVerified(false)
                .role(UserRole.USER)
                .build();

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("unverified@example.com");
        request.setPassword("password");

        given(userRepository.findByEmail("unverified@example.com")).willReturn(Optional.of(notVerifiedUser));

        assertThatThrownBy(() -> authService.login(request.getEmail(), request.getPassword()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.EMAIL_NOT_VERIFIED.getMessage());
    }

    @Test
    @DisplayName("ログイン_失敗_パスワード不一致")
    void ログイン_失敗_パスワード不一致() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("wrongpass@example.com");
        request.setPassword("wrong");

        given(userRepository.findByEmail("wrongpass@example.com")).willReturn(Optional.of(verifiedUser));
        given(passwordEncoder.matches("wrong", "encodedPassword")).willReturn(false);

        assertThatThrownBy(() -> authService.login(request.getEmail(), request.getPassword()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }
}
