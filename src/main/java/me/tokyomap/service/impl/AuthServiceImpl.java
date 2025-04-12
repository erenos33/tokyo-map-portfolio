package me.tokyomap.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.security.JwtTokenProvider;
import me.tokyomap.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String MSG_USER_NOT_FOUND = "존재하지 않는 유저입니다.";
    private static final String MSG_EMAIL_NOT_VERIFIED = "이메일 인증이 완료되지 않았습니다.";
    private static final String MSG_PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public String login(String email, String password) {

        //1. 이메일로 유저 검색
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(MSG_USER_NOT_FOUND));


        //2. 이메일 인증 확인
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException(MSG_EMAIL_NOT_VERIFIED);
        }

        //3. 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(MSG_PASSWORD_MISMATCH);
        }

        //4. JWT 토큰 발급

        return jwtTokenProvider.createToken(user.getEmail());
    }
}
