package me.tokyomap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * メール認証時に必要な情報を受け取るリクエストDTO
 * ユーザーのメールアドレスと認証コードを含む
 */
@Getter
@Setter
public class EmailVerificationRequestDto {

    @Schema(description = "ユーザーのメールアドレス", example = "test@example.com")
    private String email;

    @Schema(description = "認証コード", example = "1f3e5g")
    private String code;
}
