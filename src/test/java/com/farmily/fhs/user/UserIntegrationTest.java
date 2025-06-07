package com.farmily.fhs.auth;

import com.farmily.fhs.auth.service.UserService;
import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final String TEST_USERNAME = "deleteTestUser";

    @BeforeEach
    void setUp() {
        // 建立測試用戶
        if (userRepository.findByUsername(TEST_USERNAME).isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .username(TEST_USERNAME)
                    .password("dummy")
                    .email("test@example.com")
                    .phone("0912345678")
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

    // ⚠️ 模擬資料庫錯誤需要 Mock，可視需求擴充為單元測試
}
