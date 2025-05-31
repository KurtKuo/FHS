package com.farmily.fhs.auth;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import com.farmily.fhs.common.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ JWT 驗證整合測試：
 * 測試 Spring Security 結合 JWT 的登入驗證流程與授權保護。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "password";

    /**
     * 🔧 測試前建立測試帳號資料
     */
    @BeforeEach
    void setup() {
        userRepository.findByUsername(TEST_USERNAME)
                .ifPresent(existing -> {
                    userRepository.delete(existing);
                    userRepository.flush();
                });

        String encodedPassword = passwordEncoder.encode(TEST_PASSWORD);
        System.out.println("🔐 建立測試帳號密碼（加密後）: " + encodedPassword);

        UserEntity user = UserEntity.builder()
                .username(TEST_USERNAME)
                .password(encodedPassword)
                .email("test@example.com")
                .phone("0912345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.saveAndFlush(user);

        System.out.println("📥 資料庫加密密碼: " + user.getPassword());
        System.out.println("✅ 密碼比對結果: " +
                passwordEncoder.matches(TEST_PASSWORD, user.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USERNAME);
        System.out.println("UserDetails 密碼：" + userDetails.getPassword());
        System.out.println("資料庫密碼：" + userRepository.findByUsername(TEST_USERNAME).get().getPassword());
    }

    @AfterEach
    void cleanup() {
        userRepository.findByUsername(TEST_USERNAME)
                .ifPresent(userRepository::delete);
        userRepository.flush();
    }

    @DisplayName("✅ 正常流程測試：登入成功並可存取受保護資源")
    @Test
    void loginAndAccessProtectedEndpoint() {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        String token = loginResponse.getBody().getToken();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/user/profile", HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Hello"));
    }

    @DisplayName("🚫 登入失敗：使用錯誤密碼應回傳 UNAUTHORIZED")
    @Test
    void loginWithInvalidPasswordShouldFail() {
        LoginRequest request = new LoginRequest(TEST_USERNAME, "wrongPassword");
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @DisplayName("🚫 未帶 Token 存取受保護資源應回傳 UNAUTHORIZED")
    @Test
    void accessProtectedEndpointWithoutTokenShouldFail() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/hello", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @DisplayName("🚫 使用不合法 Token 存取受保護資源應回傳 UNAUTHORIZED")
    @Test
    void accessWithInvalidTokenShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.token.value");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/hello", HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @DisplayName("🔐 測試密碼加密與載入使用者資料")
    @Test
    void testLoadUserAndEncodePassword() {
        String encoded = passwordEncoder.encode("123456");
        System.out.println("測試加密密碼：" + encoded);

        try {
            var user = userDetailsService.loadUserByUsername("admin");
            System.out.println("查到使用者 admin，密碼：" + user.getPassword());
        } catch (Exception e) {
            System.out.println("找不到 admin 使用者：" + e.getMessage());
        }
    }

    @DisplayName("🚫 使用過期 Token 存取受保護資源應回傳 401")
    @Test
    void accessWithExpiredTokenShouldFail() {
        // 生成一個已過期的 token (過期時間設 -1 分鐘)
        String expiredToken = generateTokenWithCustomExpiration("testuser", -60 * 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(expiredToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/user/profile", HttpMethod.GET, request, String.class);

        // 401
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // 反射調用 generateTokenWithCustomExpiration
    private String generateTokenWithCustomExpiration(String username, long expirationMillis) {
        try {
            Method method = JwtService.class.getDeclaredMethod("generateTokenWithCustomExpiration", String.class, long.class);
            method.setAccessible(true);
            return (String) method.invoke(jwtService, username, expirationMillis);
        } catch (NoSuchMethodException e) {
            // 如果方法不存在，自己手動模擬一下
            return generateManualTokenWithExpiration(username, expirationMillis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 手動用 jwtService 的 key 跟 jjwt 生成自訂過期 token（如果沒有原生方法）
    private String generateManualTokenWithExpiration(String username, long expirationMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(jwtService.getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @DisplayName("🚫 非 ADMIN 角色存取 /api/admin/dashboard 應回傳 FORBIDDEN (403)")
    @Test
    void accessAdminDashboardWithoutAdminRoleShouldReturnForbidden() {
        // 登入取得非 ADMIN 角色 token
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        String token = loginResponse.getBody().getToken();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 呼叫 /api/admin/dashboard
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/admin/dashboard", HttpMethod.GET, request, String.class);

        // 預期 403 Forbidden
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
