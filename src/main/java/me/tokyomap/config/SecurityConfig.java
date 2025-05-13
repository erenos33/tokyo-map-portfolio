package me.tokyomap.config;

import me.tokyomap.security.JwtAuthenticationFilter;
import me.tokyomap.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * パスワードのハッシュ化に使用されるエンコーダー（BCrypt方式）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Securityのメイン設定
     * JWTベースの認証、認可、CORS・CSRF設定などを行う
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http
                .cors(cors -> {}) // CORS設定（CorsConfigにて定義）
                .csrf(csrf -> csrf.disable()) // CSRF無効化（JWT利用のため）
                .headers(AbstractHttpConfigurer::disable)
                .formLogin(form -> form.disable()) // フォームログイン無効化
                .httpBasic(httpBasic -> httpBasic.disable()) // Basic認証無効化
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // セッションを使用しない（JWTベース）

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint()) // 未認証時のハンドラー
                        .accessDeniedHandler(accessDeniedHandler()) // アクセス拒否時のハンドラー
                )

                .authorizeHttpRequests(auth -> auth
                        // 管理者専用エンドポイント
                        .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")

                        // 認証不要エンドポイント
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*/comments/tree").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/location").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/location/next").permitAll()
                        .requestMatchers(
                                "/api/users/**",
                                "/api/email/send",
                                "/api/email/verify",
                                "/api/reviews/*/likes/count",
                                "/api/locations/**",
                                "/api/maps/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        //  認証が必要なエンドポイント
                        .requestMatchers(HttpMethod.POST, "/api/restaurants").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/my").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/register/google").authenticated()
                        .anyRequest().authenticated()
                )

                // JWT認証フィルターをSpring Securityのフィルター前に追加
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 認証マネージャーのBean登録
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 未認証アクセス時のレスポンス設定
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("""
            {
              "errorCode": "AUTH_REQUIRED",
              "message": "ログインしたユーザーのみ利用可能です。"
            }
        """);
        };
    }

    /**
     * アクセス拒否時のレスポンス設定
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("""
            {
              "errorCode": "ACCESS_DENIED",
              "message": "アクセス権限がありません。"
            }
        """);
        };
    }
}