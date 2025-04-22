package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.EmailVerificationRequestDto;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 인증 API")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "이메일 인증 메일 발송", description = "입력한 이메일 주소로 인증 메일을 보냅니다.")
    public ApiResponse<Void> sendVerificationEmail(@RequestParam String email) {
        emailService.sendVerificationEmail(email);
        return ApiResponse.success();
    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증 확인", description = "사용자가 받은 인증 코드를 확인하고 인증 상태를 갱신합니다.")
    public ApiResponse<Void>  verifyEmailCode(@RequestBody EmailVerificationRequestDto requestDto) {
        emailService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());
        return ApiResponse.success();
    }
}
