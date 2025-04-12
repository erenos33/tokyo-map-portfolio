package me.tokyomap.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        
        //1. 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        //2. 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //3.토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            //4. 인증 객체 생성 및 SecurityContext에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //5. 다음 필터로 넘기기(필수!)
        filterChain.doFilter(request, response);
    }


    //Authorization 헤더에서 토큰 꺼내는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); //"Bearer " 이후 부분만 자름
        }
        return null;
    }
}
