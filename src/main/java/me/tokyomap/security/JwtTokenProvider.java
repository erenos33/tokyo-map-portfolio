package me.tokyomap.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWTトークンの生成・検証・解析を行うプロバイダークラス
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Key secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * アプリケーション起動時にシークレットキーを初期化
     */
    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("auth", role) // 올바름. ex: "ROLE_USER"//권한 정보 추가
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * トークンからユーザーのメールアドレス（subject）を取得する
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * トークンの署名および有効期限を検証する
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 現在時刻からのトークンの有効期限を計算して返す
     */
    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    /**
     * JWTの署名検証に使用するシークレットキーを取得する
     */
    public Key getSecretkey() {
        return secretKey;
    }
}
