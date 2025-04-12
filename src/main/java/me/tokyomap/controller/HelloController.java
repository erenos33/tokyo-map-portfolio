package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "테스트용 API", description = "JWT 인증 테스트용 API")
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "JWT 인증 테스트", description = "JWT 토큰이 유효할 경우에만 호출 가능")
    public ResponseEntity<String> hello(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok("안녕하세요, 인증된 사용자:  " + email);

    }
}
