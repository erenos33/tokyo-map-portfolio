package me.tokyomap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    @Schema(description = "토큰 만료 시각", example = "2025-04-12T13:40:00.000+09:00")
    private Date expiresAt;
}
