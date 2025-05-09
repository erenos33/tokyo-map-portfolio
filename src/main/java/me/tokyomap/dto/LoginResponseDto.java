package me.tokyomap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
public class LoginResponseDto {

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    @Schema(description = "토큰 만료 시각", example = "2025-04-12T13:40:00.000+09:00")
    private Date expiresAt;

    @Schema(description = "사용자 권한", example = "USER") // ✅ 추가
    private String role;  // ← String으로 받되, Enum에서 이름을 추출해 전달

    @Schema(description = "이메일 인증 여부")
    private boolean emailVerified;      // ← 이 부분 추가


    /**
     * 기존 AuthServiceImpl의 호출(new LoginResponseDto(token, expiresAt, role))을
     * 그대로 살리기 위한 3-인자 생성자.
     * emailVerified는 항상 true로 설정됩니다.
     */
    public LoginResponseDto(String accessToken, Date expiresAt, String role) {
        this(accessToken, expiresAt, role, true);
    }

    /**
     * 전체 필드를 초기화하는 4-인자 생성자.
     */
    public LoginResponseDto(String accessToken, Date expiresAt, String role, boolean emailVerified) {
        this.accessToken    = accessToken;
        this.expiresAt      = expiresAt;
        this.role           = role;
        this.emailVerified  = emailVerified;
    }
}
