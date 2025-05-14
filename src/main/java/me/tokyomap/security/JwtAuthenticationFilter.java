package me.tokyomap.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWTを使用してリクエストごとにユーザー認証を行うSpring Securityのフィルター
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 各リクエストに対してJWTトークンを検証し、認証情報をSecurityContextに登録する
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        
        //1. AuthorizationヘッダーからJWTトークンを取得
        String token = resolveToken(request);

        //2. トークンが有効な場合、認証情報を抽出
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);

            // JWTのクレームからロール情報を取得
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtTokenProvider.getSecretkey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("auth", String.class); //ex: "ROLE_ADMIN"
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // 3. 認証トークンを作成し、SecurityContextに設定
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 4. フィルターチェーンを継続
        filterChain.doFilter(request, response);
    }

    /**
     * AuthorizationヘッダーからBearerトークンを抽出する補助メソッド
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); //"Bearer " 이후 부분만 자름
        }
        return null;
    }
}
