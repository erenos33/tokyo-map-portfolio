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
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 관리자 전용
                        .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")

                        // 인증 없이 접근 허용
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

                        // 그 외 요청 (등록, 내 등록 조회, 삭제 등)는 인증 필요
                        .requestMatchers(HttpMethod.POST, "/api/restaurants").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/my").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/register/google").authenticated()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}