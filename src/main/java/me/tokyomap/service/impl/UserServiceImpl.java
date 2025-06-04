package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.UserMapper;
import me.tokyomap.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ユーザー登録・認証状態確認に関するサービスの実装クラス
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 新規ユーザーを登録する（メール重複チェックとパスワード暗号化を含む）
     */
    @Override
    public void registerUser(UserRegisterRequestDto requestDto) {

        if(userRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = UserMapper.toEntity(requestDto, passwordEncoder);
        userRepository.save(user);

    }

    /**
     * 指定されたメールアドレスのユーザーがメール認証済みかどうかを判定する
     */
    @Override
    public boolean isEmailVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.isEmailVerified();
    }
}
