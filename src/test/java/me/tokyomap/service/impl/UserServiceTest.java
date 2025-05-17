package me.tokyomap.service.impl;

import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void ユーザー登録_成功() {
        // given
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password123!");
        dto.setNickname("テスター");

        given(userRepository.existsByEmail(dto.getEmail())).willReturn(false);
        given(passwordEncoder.encode(dto.getPassword())).willReturn("encodedPassword");

        User mockUser = mock(User.class);
        try (MockedStatic<UserMapper> mocked = Mockito.mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto, passwordEncoder)).thenReturn(mockUser);

            // when
            userService.registerUser(dto);

            // then
            then(userRepository).should().save(mockUser);
        }
    }

    @Test
    void ユーザー登録_重複メール_失敗() {
        // given
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setEmail("duplicate@example.com");
        dto.setPassword("pw123");
        dto.setNickname("重複ユーザー");

        given(userRepository.existsByEmail(dto.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

    @Test
    void メール認証_済みのユーザー_成功() {
        // given
        String email = "verified@example.com";
        User user = mock(User.class);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(user.isEmailVerified()).willReturn(true);

        // when
        boolean result = userService.isEmailVerified(email);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void メール認証_ユーザーが存在しない_例外発生() {
        // given
        String email = "unknown@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.isEmailVerified(email))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
