package com.farmily.fhs.common.config;

import com.farmily.fhs.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration                      // 標示為 Spring 設定類別
@EnableWebSecurity                 // 啟用 Spring Security 網路安全功能
@RequiredArgsConstructor           // 自動為 final 欄位產生建構子（注入 JwtAuthenticationFilter、AuthenticationProvider）
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;  // 自定義 JWT 驗證過濾器
    private final AuthenticationProvider authenticationProvider;  // 自定義的驗證提供者，通常連結 CustomUserDetailsService

    /**
     * 註冊 SecurityFilterChain：Spring Security 的主設定入口
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1️⃣ 停用 CSRF：因為是 RESTful API，不使用 Cookie 驗證，因此不需 CSRF 防護
                .csrf(AbstractHttpConfigurer::disable)

                // 2️⃣ 不使用 session：改用 JWT，因此每個 request 都是 Stateless（無狀態）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3️⃣ 設定授權規則
                .authorizeHttpRequests(auth -> auth
                        // 允許不帶 Token 就能存取 /api/auth/**（如 /login, /register）
                        .requestMatchers("/api/auth/**").permitAll()
                        // 其他所有請求都需要驗證
                        .anyRequest().authenticated()
                )

                // 4️⃣ 指定驗證提供者：例如使用自定義的 CustomUserDetailsService
                .authenticationProvider(authenticationProvider)

                // 5️⃣ 在原生的 UsernamePasswordAuthenticationFilter 之前插入 JWT 過濾器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
