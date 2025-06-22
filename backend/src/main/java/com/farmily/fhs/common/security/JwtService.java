package com.farmily.fhs.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j // 啟用 Lombok 的 SLF4J Logger
public class JwtService {

    // JWT 有效期限，這裡設定一天 (毫秒)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 天

    @Getter
    private Key key;

    // 從 application.yml 或 properties 注入密鑰字串，建議 256-bit Base64 字串
    @Value("${jwt.secret}")
    private String secretKeyString;

    // 啟動時用字串產生 Key 物件
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        log.info("🔑 JWT Key 初始化完成");
    }

    /**
     * 根據 Authentication 物件生成 JWT token (內含 username)
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        log.debug("🔐 產生 JWT Token，使用者: {}", username);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 根據 username 產生 JWT（如果你想用 username 字串呼叫）
     */
    public String generateToken(String username) {
        log.debug("🔐 產生 JWT Token，使用者: {}", username);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 從 JWT 解析出 username (subject)
     */
    public String extractUsername(String token) {
        try {
            String username = parseClaims(token).getSubject();
            log.debug("🔍 從 token 解析出 username: {}", username);
            return username;
        } catch (Exception e) {
            log.error("❌ 解析 token username 失敗: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 驗證 JWT 是否有效且與 UserDetails 中的 username 一致
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        log.debug("🔐 Token 驗證結果: {}", isValid);
        return isValid;
    }

    /**
     * 檢查 JWT 是否已過期
     */
    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = parseClaims(token).getExpiration();
            boolean expired = expiration.before(new Date());
            if (expired) {
                log.warn("❌ Token 已過期");
            }
            return expired;
        } catch (Exception e) {
            log.error("❌ 解析 token 過期時間失敗: {}", e.getMessage());
            return true; // 解析失敗當作過期處理
        }
    }

    /**
     * 解析 JWT Claims，失敗會拋例外
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
