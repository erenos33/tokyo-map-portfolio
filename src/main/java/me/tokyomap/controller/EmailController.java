package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.EmailVerificationRequestDto;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.service.EmailService;
import org.springframework.web.bind.annotation.*;


/**
 * メール認証に関するAPIを提供するコントローラー
 */
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "メール認証", description = "メール認証に関するAPI")
public class EmailController {

    private final EmailService emailService;

    /**
     * 入力されたメールアドレス宛に認証メールを送信
     */
    @PostMapping("/send")
    @Operation(
            summary = "認証メールの送信",
            description = "入力されたメールアドレス宛に認証コードを含むメールを送信します。"
    )
    public ApiResponse<Void> sendVerificationEmail(@RequestParam String email) {
        emailService.sendVerificationEmail(email);
        return ApiResponse.success();
    }

    /**
     * 認証コードを確認し、ユーザーの認証状態を更新
     */
    @PostMapping("/verify")
    @Operation(
            summary = "認証コードの確認",
            description = "受信した認証コードを確認し、メール認証の状態を更新します。"
    )
    public ApiResponse<Void>  verifyEmailCode(@RequestBody EmailVerificationRequestDto requestDto) {
        emailService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());
        return ApiResponse.success();
    }
}
