package me.tokyomap.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.service.EmailService;
import me.tokyomap.util.EntityFinder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * メール認証に関連する処理を提供するサービス実装クラス
 * 認証コードの生成・送信・検証を担当
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    /**
     * ユーザーのメールアドレスに6桁の認証コードを生成して送信する
     * 有効期限は10分間。コードはDBに保存される
     */
    @Override
    public void sendVerificationEmail(String toEmail) {

        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        User user = EntityFinder.getUserOrThrow(userRepository, toEmail);

        user.updateVerificationCode(verificationCode, expiresAt);
        userRepository.save(user);

        String subject = "グルメマップ：メール認証";
        String content = "<h1>メール認証を完了してください</h1>" +
                "<p>以下の認証コードを入力してください：</p>" +
                "<h2>" + verificationCode + "</h2>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 入力された認証コードを検証し、正当であればユーザーを認証済みにする
     * 有効期限切れとコード不一致をそれぞれ検出して例外を投げる
     */
    @Override
    public void verifyEmailCode(String email, String code) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        if(user.getVerificationCodeExpiresAt() == null ||
        user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        if (user.getVerificationCode() == null ||
                !code.trim().equalsIgnoreCase(user.getVerificationCode().trim())) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        user.verifyEmail();

    }
}
