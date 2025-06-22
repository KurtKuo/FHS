package com.farmily.fhs.common.config;

import com.farmily.fhs.common.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 📦 表示這是一個 Spring 的設定類別（用來註冊 Security Bean）
@EnableWebSecurity // 🔐 啟用 Spring Security 的網路安全功能
@RequiredArgsConstructor // 💡 自動注入 final 欄位（如 jwtAuthFilter、userDetailsService）
@Slf4j // ✅ 使用 SLF4J 日誌
public class SecurityConfig {

    // 🔒 自訂 JWT 驗證過濾器：處理 token 的解析與使用者驗證
    private final JwtAuthenticationFilter jwtAuthFilter;

    // 🔐 Spring Security 的 UserDetailsService，從資料庫讀取使用者資訊
    private final UserDetailsService userDetailsService;

    /**
     * 🔑 密碼加密器：用來加密使用者密碼，並在登入時做比對（必須與註冊時一致）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 可設定 strength（預設 10）
    }

    /**
     * 🧠 驗證提供者：負責帳號密碼驗證邏輯（由 Spring Security 呼叫）
     * 結合 UserDetailsService 與 PasswordEncoder 來查詢帳號並驗證密碼。
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 使用自訂的帳號查詢邏輯
        authProvider.setPasswordEncoder(passwordEncoder());     // 密碼加密驗證器
        return authProvider;
    }

    /**
     * 🔁 驗證管理器：Spring Security 的核心，執行實際的認證流程（如手動驗證）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // 自動綁定 authenticationProvider
    }

    /**
     * 🔧 安全過濾器鏈設定：這是 Spring Security 的主要安全規則設定
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🚫 停用 CSRF（跨站請求偽造）：因為使用 JWT 而非 Cookie，不需要 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 📭 不建立 Session：每次請求都由 JWT 驗證，不記住登入狀態
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 📜 授權規則：哪些路徑放行、哪些要驗證
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/menus/**").permitAll()          // 菜單 不需要登入
                        .requestMatchers("/api/auth/**").permitAll()           // 登入/註冊 不需要登入
                        .requestMatchers("/error").permitAll()                 // ⭐ 防止 403 fallback 到 /error 再被當匿名 user
                        .requestMatchers("/api/admin/**").hasRole("BOSS")      // 只有 ADMIN 角色
                        .requestMatchers("/api/user/profile").authenticated()  // 需要登入
                        .anyRequest().authenticated()                                   // 其餘皆需驗證
                )

                // 🔐 使用自定義的帳號密碼驗證邏輯（authenticationProvider Bean）
                .authenticationProvider(authenticationProvider())

                // 🧱 在帳號密碼驗證之前加入 JWT 驗證（即：先看有沒有 JWT token）
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 🚨 錯誤處理：未認證 vs 權限不足
                .exceptionHandling(exception -> exception

                        // 🔐 未通過認證：沒帶 token 或 token 無效 → 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("🚫 未認證：回傳 401 Unauthorized");
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })

                        // ⛔ 已認證但權限不足 → 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("⛔ 權限不足：回傳 403 Forbidden");
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                            response.flushBuffer(); // ⭐ 關鍵！強制送出，不進入 /error
                        })
                );

        // ✅ 返回整個安全過濾器鏈
        return http.build();
    }
}
