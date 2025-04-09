package me.tokyomap.domain.user.entity;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.domain.user.role.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class UserTest {

    @Autowired
    private UserRepository userRepository;



    @Test
    void saveTest() {
        User user = User.builder()
                .email("erenos33@gmail.com")
                .password("12345555")
                .nickname("eree")
                .emailVerified(false)
                .role(UserRole.USER)
                .build();
        User user1 = User.builder()
                .email("erenos3333@gmail.com")
                .password("12345555")
                .nickname("erene")
                .emailVerified(false)
                .role(UserRole.USER)
                .build();
        User user2 = User.builder()
                .email("erenos3343433@gmail.com")
                .password("12345555")
                .nickname("eren34o")
                .emailVerified(false)
                .role(UserRole.USER)
                .build();
        User user3 = User.builder()
                .email("erenos333543433@gmail.com")
                .password("12345555")
                .nickname("eren34se")
                .emailVerified(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Optional<User> userOptional = userRepository.findByEmail("erenos33@gmail.com");
        Optional<User> user1Optional = userRepository.findByEmail("erenos3333@gmail.com");

        assertThat(userRepository.findByEmail("erenos33@gmail.com")).isPresent();
        assertThat(userOptional.get().getEmail()).isEqualTo("erenos33@gmail.com");
        log.info("userOptional: {}", userOptional.get().getEmail());
        log.info("user1Optional: {}", user1Optional.get().getEmail());

    }

}