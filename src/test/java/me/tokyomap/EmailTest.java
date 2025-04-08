package me.tokyomap;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import me.tokyomap.domain.user.entity.QUser;
import me.tokyomap.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class EmailTest {
    @Autowired
    private EntityManager em;


    @Test
    void querydslTest() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QUser qUser = QUser.user;

        List<User> result = query
                .selectFrom(qUser)
                .where(qUser.email.eq("erenos33@gmail.com"))
                .fetch();

        System.out.println(result);
    }


}
