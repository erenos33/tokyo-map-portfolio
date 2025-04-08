package me.tokyomap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EmailTest {
    @Autowired
    private JavaMailSender mailSender;


    @Test
    public void sendTestMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("erenos33@gmail.com");
        message.setSubject("테스트 메일");
        message.setText("이건 도쿄 맛집 포트폴리오 프로젝트에서 보낸 테스트 메일이에요!");
        mailSender.send(message);
    }
}
