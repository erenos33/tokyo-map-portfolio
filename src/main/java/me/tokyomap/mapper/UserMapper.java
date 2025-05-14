package me.tokyomap.mapper;

import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.role.UserRole;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * UserRegisterRequestDtoをUserエンティティに変換するマッパークラス
 */
public class UserMapper {
    /**
     * 登録リクエストDTOとパスワードエンコーダを使用してUserエンティティを生成する
     */
    public static User toEntity(UserRegisterRequestDto dto, PasswordEncoder encoder) {
        return User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .emailVerified(false)
                .role(UserRole.USER)
                .build();
    }
}