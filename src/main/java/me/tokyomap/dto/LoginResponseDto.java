package me.tokyomap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

/**
 * ログイン成功時に返されるトークン情報などを含むレスポンスDTO
 */
@Getter
public class LoginResponseDto {

    @Schema(description = "JWTアクセストークン", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    @Schema(description = "トークンの有効期限", example = "2025-04-12T13:40:00.000+09:00")
    private Date expiresAt;

    @Schema(description = "ユーザー権限", example = "USER") // ✅ 추가
    private String role;

    @Schema(description = "メール認証済みかどうかを示すフラグ")
    private boolean emailVerified;


    /**
     * アクセストークン・有効期限・ロール情報のみを指定してレスポンスを作成するコンストラクタ。
     * メール認証済み状態は常に true に設定される。
     */
    public LoginResponseDto(String accessToken, Date expiresAt, String role) {
        this(accessToken, expiresAt, role, true);
    }

    /**
     * 全てのフィールドを明示的に初期化するためのコンストラクタ。
     * メール認証状態を含めて任意に設定可能。
     */
    public LoginResponseDto(String accessToken, Date expiresAt, String role, boolean emailVerified) {
        this.accessToken    = accessToken;
        this.expiresAt      = expiresAt;
        this.role           = role;
        this.emailVerified  = emailVerified;
    }
}
