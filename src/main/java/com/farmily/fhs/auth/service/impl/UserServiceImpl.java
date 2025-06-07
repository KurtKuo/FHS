package com.farmily.fhs.auth.service.impl;

import com.farmily.fhs.auth.service.UserService;
import com.farmily.fhs.common.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j // ✅ 啟用 SLF4J 日誌功能
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
}
