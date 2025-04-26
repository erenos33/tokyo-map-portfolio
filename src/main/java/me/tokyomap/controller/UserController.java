package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import me.tokyomap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Register", description = "Register Test")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새 사용자를 등록합니다.")
    public ApiResponse<String> registUser(@RequestBody @Valid UserRegisterRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ApiResponse.success("회원가입이 완료되었습니다.");
    }
}
