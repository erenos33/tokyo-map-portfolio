package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import me.tokyomap.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * ユーザー登録を処理するAPIコントローラー
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "ユーザー登録", description = "新規ユーザーの登録処理を行うAPI")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * ユーザー登録（会員登録）
     */
    @PostMapping("/register")
    @Operation(
            summary = "ユーザー登録",
            description = "新規ユーザーを登録し、正常に完了した場合はメッセージを返します。"
    )
    public ApiResponse<String> registUser(@RequestBody @Valid UserRegisterRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ApiResponse.success("ユーザー登録が完了しました。");
    }
}
