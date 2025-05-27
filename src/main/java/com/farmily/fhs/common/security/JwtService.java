package com.farmily.fhs.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // JWT 有效期限，這裡設定一天 (毫秒)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 天

    // 產生 HS256 對稱簽章的金鑰，建議部署時改用安全環境參數注入
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * 根據 username 產生 JWT，內含發行時間與過期時間
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 放入 username 為 subject
                .setIssuedAt(new Date()) // 發行時間
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 過期時間
                .signWith(key) // 簽章
                .compact();
    }

    /**
     * 從 JWT 解析出 username (subject)
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 用同一組金鑰驗證簽章
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 驗證 JWT 是否有效且與 UserDetails 中的 username 一致
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 檢查 JWT 是否已過期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
