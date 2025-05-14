package me.tokyomap.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * ログイン時に必要なユーザー認証情報を送信するためのリクエストDTO
 * メールアドレスとパスワードを含む
 */
@Getter
@Setter
@Schema(description = "ログインリクエストDTO")
public class LoginRequestDto {

    @Schema(description = "ユーザーのメールアドレス", example = "erenos33@gmail.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "パスワード（8〜20文字）", example = "12345678")
    @NotBlank
    private String password;
}
