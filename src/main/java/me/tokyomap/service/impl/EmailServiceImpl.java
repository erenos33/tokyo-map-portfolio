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

@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Override
    public void sendVerificationEmail(String toEmail) {
        //1. 인증 코드 생성
        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        //2. 유저 조회
        User user = EntityFinder.getUserOrThrow(userRepository, toEmail);

        //3. 인증 코드 저장
        user.updateVerificationCode(verificationCode, expiresAt);
        userRepository.save(user);//setter 대신 명확한 메서드 사용

        //4. 이메일 내용 구성
        String subject = "도쿄 맛집 포트폴리오 이메일 인증";
        String content = "<h1>이메일 인증을 완료해주세요</h1>" +
                "<p>아래 인증 코드를 입력해주세요:</p>" +
                "<h2>" + verificationCode + "</h2>";

        //5. 메일 발송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); //HTML true

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public void verifyEmailCode(String email, String code) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        //1. 유효시간 만료 확인
        if(user.getVerificationCodeExpiresAt() == null ||
        user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        //2. 코드 일치 확인
        if (user.getVerificationCode() == null ||
                !code.trim().equalsIgnoreCase(user.getVerificationCode().trim())) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        //3. 인증 처리
        user.veriFyEmail();

    }
}
