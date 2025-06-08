package com.farmily.fhs.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ✅ 密碼 驗證整合測試：
 * 測試 Spring Security 結合 JWT 的登入驗證流程與授權保護。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EncoderTest {

    @Test
    @DisplayName("✅ 驗證原始密碼與加密密碼應比對成功")
    void passwordShouldMatchEncoded() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encodedPassword = encoder.encode(rawPassword); // 模擬註冊時加密

        // 驗證登入時密碼是否一致
        assertTrue(encoder.matches(rawPassword, encodedPassword), "密碼比對應該成功");
    }

    @Test
    @DisplayName("🚫 錯誤密碼不應比對成功")
    void passwordShouldNotMatchWrongEncoded() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String wrongPassword = "wrongPassword";
        String encodedPassword = encoder.encode(rawPassword);

        // 錯誤密碼比對應失敗
        assertFalse(encoder.matches(wrongPassword, encodedPassword), "密碼比對應該失敗");
    }

    @Test
    @DisplayName("🧪 驗證 encode 結果格式正確（以 $2a$ 開頭）")
    void encodedPasswordFormatShouldBeValid() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encoded = encoder.encode(rawPassword);

        assertTrue(encoded.startsWith("$2a$"), "BCrypt 加密結果應該以 $2a$ 開頭");
    }
}
