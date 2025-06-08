package com.farmily.fhs.auth.service.impl;

import com.farmily.fhs.auth.dto.ChangePasswordRequest;
import com.farmily.fhs.auth.service.UserService;
import com.farmily.fhs.common.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 使用者服務實作：處理與帳號相關的邏輯（刪除帳號、變更密碼）
 */
@Service
@Slf4j // ✅ 啟用 SLF4J 日誌功能
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 刪除指定使用者帳號
     *
     * @param username 使用者帳號（從登入 JWT token 中取得）
     * @throws RuntimeException 若使用者不存在或刪除失敗
     */
    @Override
    public void deleteUser(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            try {
                log.info("🔐 刪除使用者：{}", username);
                userRepository.delete(user);
            } catch (Exception e) {
                // ⛔ 刪除時出現資料庫錯誤或其他例外
                throw new RuntimeException("刪除使用者失敗，請稍後再試", e);
            }
        }, () -> {
            // ⛔ 使用者不存在
            throw new RuntimeException("找不到使用者帳號");
        });
    }

    /**
     * 更改目前使用者的密碼
     *
     * @param username 使用者帳號（從登入 JWT token 中取得）
     * @param request  請求內容，包含舊密碼與新密碼
     * @throws IllegalArgumentException 若舊密碼不正確
     * @throws RuntimeException 若使用者不存在
     */
    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {

            // 🧪 驗證使用者提供的舊密碼是否正確
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                log.warn("❌ 使用者 {} 輸入錯誤的舊密碼", username);
                throw new IllegalArgumentException("舊密碼錯誤");
            }

            // 🔐 將新密碼進行加密後存入資料庫
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            log.info("🔁 使用者 {} 成功更新密碼", username);

        }, () -> {
            // ⛔ 找不到該使用者帳號
            throw new RuntimeException("找不到使用者帳號");
        });
    }
}
