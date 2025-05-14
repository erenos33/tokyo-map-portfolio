package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.LoginResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.security.JwtTokenProvider;
import me.tokyomap.service.AuthService;
import me.tokyomap.util.EntityFinder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 認証に関するサービスの実装クラス
 * ユーザーのログイン認証、トークン発行、検証済み状態の確認などを行う
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * ログイン処理を行い、JWTトークンと有効期限を返す
     * ユーザー検証 → メール認証チェック → パスワード一致検証の順で処理
     */
    @Override
    public LoginResponseDto login(String email, String password) {

        User user = EntityFinder.getUserOrThrow(userRepository, email);

        if (!user.isEmailVerified()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
        Date expiresAt = jwtTokenProvider.getExpirationDate();

        return new LoginResponseDto(token, expiresAt, user.getRole().name());
    }
}
