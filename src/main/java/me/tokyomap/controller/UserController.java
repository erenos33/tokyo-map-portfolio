package me.tokyomap.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.user.UserRegisterRequestDto;
import me.tokyomap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registUser(@RequestBody @Valid UserRegisterRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok("회원가입이 안료되었습니다.");
    }
}
