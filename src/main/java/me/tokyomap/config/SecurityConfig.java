package me.tokyomap.config;

import me.tokyomap.security.JwtAuthenticationFilter;
import me.tokyomap.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .headers(AbstractHttpConfigurer::disable)
                .formLogin(form -> form.disable()) //로그인 패스워드 로그 삭제
                .httpBasic(httpBasic -> httpBasic.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ✅ 관리자 전용 API
                        .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")

                        // ✅ 로그인 API만 명확히 permitAll
                        .requestMatchers("/api/auth/login").permitAll()

                        // ✅ 댓글 조회는 인증 불필요
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*/comments/tree").permitAll()

                        // ✅ 테스트용 인증 API
                        .requestMatchers("/api/auth/test").authenticated()

                        // ✅ 비회원 접근 허용 API
                        .requestMatchers(
                                "/api/users/**",
                                "/api/email/send",
                                "/api/email/verify",
                                "/api/restaurants/**",
                                "/api/reviews/*/likes/count",
                                "/api/locations/**",
                                "/api/maps/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        // ✅ 그 외는 인증 필요
                        .anyRequest().authenticated()
                )

//나머지는 인증 필요
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
