package com.farmily.fhs.admin;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminAccessIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String ADMIN_USERNAME = "adminuser";
    private final String ADMIN_PASSWORD = "adminpass";

    @BeforeEach
    void setup() {
        // 確保 admin 帳號存在
        userRepository.findByUsername(ADMIN_USERNAME).ifPresent(userRepository::delete);
        userRepository.flush();

        UserEntity admin = UserEntity.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .email("admin@example.com")
                .phone("0999999999")
                .role("ROLE_ADMIN") // ⭐ 關鍵：角色需為 ROLE_ADMIN
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.saveAndFlush(admin);
    }

    @AfterEach
    void cleanup() {
        userRepository.findByUsername(ADMIN_USERNAME).ifPresent(userRepository::delete);
        userRepository.flush();
    }

    @Test
    @DisplayName("✅ 管理員可成功存取 /api/admin/dashboard")
    void adminAccessShouldSucceed() {
        // 先登入取得 JWT token
        LoginRequest loginRequest = new LoginRequest(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String token = loginResponse.getBody().getToken();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/admin/dashboard", HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Welcome Admin"));
    }

    @Test
    @DisplayName("🚫 未登入存取 /api/admin/dashboard 應回傳 401")
    void unauthorizedAccessShouldFail() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/dashboard", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("🚫 非 ADMIN 身份登入存取 /api/admin/dashboard 應回傳 403")
    void nonAdminAccessShouldReturnForbidden() {
        // 建立一個普通使用者
        String normalUsername = "normaluser";
        String normalPassword = "normalpass";

        userRepository.findByUsername(normalUsername).ifPresent(userRepository::delete);
        userRepository.flush();

        UserEntity user = UserEntity.builder()
                .username(normalUsername)
                .password(passwordEncoder.encode(normalPassword))
                .email("user@example.com")
                .phone("0888888888")
                .role("ROLE_USER") // ⭐ 不是 ADMIN
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.saveAndFlush(user);

        // 登入
        LoginRequest loginRequest = new LoginRequest(normalUsername, normalPassword);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, LoginResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String token = loginResponse.getBody().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 嘗試存取管理員路徑
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/admin/dashboard", HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        // 清理資料
        userRepository.findByUsername(normalUsername).ifPresent(userRepository::delete);
        userRepository.flush();
    }
}
