package me.tokyomap.service.impl;

import jakarta.mail.internet.MimeMessage;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void 認証メール送信_成功() throws Exception {
        // given
        String email = "test@example.com";
        User user = mock(User.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendVerificationEmail(email);

        // then
        then(user).should().updateVerificationCode(anyString(), any(LocalDateTime.class));
        then(userRepository).should().save(user);
        then(mailSender).should().send(mimeMessage);
    }

    @Test
    void 認証メール送信_失敗_例外発生時() {
        // given
        String email = "fail@example.com";
        User user = mock(User.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // メール送信中に例外が発生するように設定
        willThrow(new RuntimeException("メール送信エラー")).given(mailSender).send(any(MimeMessage.class));

        // then
        assertThatThrownBy(() -> emailService.sendVerificationEmail(email))
                .isInstanceOf(RuntimeException.class) // EmailServiceImpl에서 CustomException으로 감싸지 않았기 때문에 RuntimeException 기대
                .hasMessage("メール送信エラー");
    }

    @Test
    void 認証コード検証_成功() {
        // given
        String email = "verify@example.com";
        String code = "ABC123";
        User user = mock(User.class);

        lenient().when(user.getVerificationCode()).thenReturn("ABC123");
        lenient().when(user.getVerificationCodeExpiresAt()).thenReturn(LocalDateTime.now().plusMinutes(10));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        emailService.verifyEmailCode(email, code);

        // then
        then(user).should().verifyEmail();
    }

    @Test
    void 認証コード検証_失敗_コード不一致() {
        // given
        String email = "wrongcode@example.com";
        String code = "WRONG";
        User user = mock(User.class);

        lenient().when(user.getVerificationCode()).thenReturn("RIGHT");
        lenient().when(user.getVerificationCodeExpiresAt()).thenReturn(LocalDateTime.now().plusMinutes(10));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> emailService.verifyEmailCode(email, code))
                .hasMessage(ErrorCode.INVALID_VERIFICATION_CODE.getMessage());
    }

    @Test
    void 認証コード検証_失敗_有効期限切れ() {
        // given
        String email = "expired@example.com";
        String code = "ANY";
        User user = mock(User.class);

        lenient().when(user.getVerificationCode()).thenReturn("ANY");
        lenient().when(user.getVerificationCodeExpiresAt()).thenReturn(LocalDateTime.now().minusMinutes(5));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> emailService.verifyEmailCode(email, code))
                .hasMessage(ErrorCode.EXPIRED_VERIFICATION_CODE.getMessage());
    }
}
