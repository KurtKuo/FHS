package com.farmily.fhs.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * 這個 Filter 會攔截所有請求，
     * 從 HTTP Header 的 Authorization 讀取 Bearer Token (JWT)。
     * 解析 JWT，驗證有效性後，
     * 將認證資料放入 SecurityContext，使 Spring Security 知道使用者已登入。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 如果沒有帶 Authorization 或不是 Bearer 開頭，直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 取得 token 字串 (去掉 "Bearer " 字首)
        jwt = authHeader.substring(7);
        // 從 JWT 解出 username
        username = jwtService.extractUsername(jwt);

        // 如果有 username 且尚未認證
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 透過 UserDetailsService 載入使用者資料
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 驗證 token 是否有效 (包含簽章、過期時間)
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 建立一個認證物件並放入 SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 繼續執行 Filter 鍊
        filterChain.doFilter(request, response);
    }
}
