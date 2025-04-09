package me.tokyomap.domain.user.entity;

import jakarta.transaction.Transactional;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.domain.user.role.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveTest() {
        User user = User.builder()
                .email("erenos33@gmail.com")
                .password("1234")
                .nickname("erenose")
                .emailVerified(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

    }

}