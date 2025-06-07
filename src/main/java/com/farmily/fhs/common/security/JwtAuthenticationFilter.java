package com.farmily.fhs.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 認證過濾器，對每個請求檢查 Authorization Header 的 JWT token。
 * 驗證成功後，設定 Spring Security 的認證上下文。
 */
@Component
@Slf4j  // 啟用 lombok 的 SLF4J Logger
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.debug("📥 處理請求路徑: {}", path);

        // 跳過 /api/auth/ 的登入註冊相關請求
        if (path.startsWith("/api/auth/")) {
            log.debug("➡️ 跳過認證過濾器 (auth 路徑)");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 檢查 Authorization Header 格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ Authorization header 缺失或格式錯誤");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        log.debug("🔍 從 token 取得使用者名稱: {}", username);

        // 若尚未認證，嘗試驗證並設定 SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("🔄 從資料庫載入使用者資料...");
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            log.info("✅ 使用者載入成功: {}", userDetails.getUsername());
            log.info("🔐 使用者權限: {}", userDetails.getAuthorities());

            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("✅ Token 有效，設定認證");

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("✅ SecurityContext 已設置認證: {}",
                        SecurityContextHolder.getContext().getAuthentication().getName());
            } else {
                log.warn("❌ Token 驗證失敗 (可能是過期或簽名錯誤)");
            }
        } else {
            if (username == null) {
                log.warn("⚠️ Token 無 username");
            } else {
                log.debug("⚠️ SecurityContext 已有認證，跳過設定");
            }
        }

        log.debug("➡️ 請求繼續通過過濾器鏈...");
        log.debug("🔍 最終認證狀態: {}", SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}
