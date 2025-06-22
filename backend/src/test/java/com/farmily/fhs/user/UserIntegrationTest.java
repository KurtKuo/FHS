package com.farmily.fhs.user;

import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import com.farmily.fhs.user.dto.ChangePasswordRequest;
import com.farmily.fhs.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private final String TEST_USERNAME = "deleteTestUser";
    private final String INITIAL_PASSWORD = "InitialPass123!";
    private final String NEW_PASSWORD = "NewPass456!";

    @BeforeEach
    void setUp() {
        // 建立測試用戶，密碼需加密
        if (userRepository.findByUsername(TEST_USERNAME).isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .username(TEST_USERNAME)
                    .password(passwordEncoder.encode(INITIAL_PASSWORD))
                    .email("test@example.com")
                    .phone("0912345678")
                    .role(2) // Role.MEMBER
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        }
    }

    @AfterEach
    void cleanUp() {
        userRepository.findByUsername(TEST_USERNAME).ifPresent(userRepository::delete);
    }

    @Test
    @DisplayName("✅ 成功刪除存在的使用者")
    void shouldDeleteExistingUser() {
        assertTrue(userRepository.findByUsername(TEST_USERNAME).isPresent());

        assertDoesNotThrow(() -> userService.deleteUser(TEST_USERNAME));

        assertFalse(userRepository.findByUsername(TEST_USERNAME).isPresent());
    }

    @Test
    @DisplayName("🚫 嘗試刪除不存在的使用者應拋出錯誤")
    void shouldThrowIfUserNotFound() {
        String nonexistentUser = "nonexistent";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(nonexistentUser));

        assertEquals("找不到使用者帳號", exception.getMessage());
    }

    @Test
    @DisplayName("✅ 成功變更密碼")
    void shouldChangePasswordSuccessfully() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(INITIAL_PASSWORD);
        request.setNewPassword(NEW_PASSWORD);

        assertDoesNotThrow(() -> userService.changePassword(TEST_USERNAME, request));

        // 取出資料庫最新密碼，確認新密碼已加密且與明文不相同
        UserEntity user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertNotEquals(INITIAL_PASSWORD, user.getPassword());
        // 用 passwordEncoder 驗證新密碼
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, user.getPassword()));
    }

    @Test
    @DisplayName("🚫 舊密碼錯誤變更密碼失敗")
    void shouldThrowIfCurrentPasswordIsWrong() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword(NEW_PASSWORD);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(TEST_USERNAME, request));

        assertEquals("舊密碼錯誤", exception.getMessage());
    }

    @Test
    @DisplayName("🚫 變更密碼時找不到使用者應拋出錯誤")
    void shouldThrowIfUserNotFoundOnChangePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(INITIAL_PASSWORD);
        request.setNewPassword(NEW_PASSWORD);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.changePassword("nonexistentUser", request));

        assertEquals("找不到使用者帳號", exception.getMessage());
    }
}
