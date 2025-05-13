package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.LoginResponseDto;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.user.LoginRequestDto;
import me.tokyomap.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


/**
 * 認証関連のAPIを提供するコントローラー
 */
@Tag(name = "認証API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * ユーザーログイン（JWTトークン発行）
     */
    @PostMapping("/login")
    @Operation(
            summary = "ログイン",
            description = "メールアドレスとパスワードを使用してログインします。成功するとJWTトークンが返されます。"
    )
    public ApiResponse<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        LoginResponseDto loginResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ApiResponse.success(loginResponse);
    }

    /**
     * JWTトークンの検証用API（ログイン状態確認）
     */
    @GetMapping("/test")
    @Operation(
            summary = "JWT認証テスト",
            description = "JWTトークンが有効な場合、アクセス可能なテストAPIです。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<String> testJwtAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.success("JWT認証成功！ログインユーザー: " + email);
    }

    /**
     * 管理者専用API（ADMINロール必須）
     */
    @GetMapping("/admin/only")
    @Operation(
            summary = "管理者専用API",
            description = "このエンドポイントはADMINロールを持つユーザーのみアクセス可能です。"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> adminOnly() {
        return ApiResponse.success("管理者権限確認成功！");
    }
}
