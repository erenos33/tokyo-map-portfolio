package me.tokyomap.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 新規ユーザー登録時に必要な情報を受け取るリクエストDTO
 * メールアドレス、パスワード、ニックネームを含む
 */
@Getter
@Setter
@Schema(description = "ユーザー登録リクエストDTO")
public class UserRegisterRequestDto {

    @Schema(description = "ユーザーのメールアドレス", example = "example@example.com")
    @Email(message = "有効なメールアドレスを入力してください")
    @NotBlank(message = "メールアドレスは必須です。")
    private String email;

    @Schema(description = "パスワード（8〜20文字）", example = "12345678")
    @NotBlank(message = "パスワードは必須です。")
    @Size(min = 8, max = 20)
    private String password;

    @Schema(description = "ニックネーム（1〜10文字）", example = "東京グルメラバー")
    @NotBlank(message = "ニックネームは必須です。")
    @Size(min = 1, max = 10)
    private String nickname;
}
