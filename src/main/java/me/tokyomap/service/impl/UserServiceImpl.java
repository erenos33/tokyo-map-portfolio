package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.domain.user.role.UserRole;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import me.tokyomap.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void registerUser(UserRegisterRequestDto requestDto) {
        //1. 이메일 중복 체크
        if(userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        //2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        //3. User 엔티티 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .emailVerified(false)//이메일 인증은 아지 안해서 false
                .role(UserRole.USER)
                .build();

        //4.저장
        userRepository.save(user);

    }
}
