package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.LoginResponseDto;
import me.tokyomap.dto.user.LoginRequestDto;
import me.tokyomap.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        LoginResponseDto loginResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    @SecurityRequirement(name = "bearerAuth") // Swagger에서 Authorize 버튼 인식용
    @Operation(summary = "JWT 로그인 테스트용", description = "토큰이 있어야 호출 가능")
    @GetMapping("/test")
    public ResponseEntity<String> testJwtAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok("JWT 인증 성공! 로그인한 유저: " + email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "관리자 전용 API", description = "ADMIN 권한이 있어야 호출 가능합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("관리자 권한 확인 성공! 👑");
    }


}
