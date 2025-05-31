package com.farmily.fhs.common.security;

import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 👤 自訂使用者服務：提供 Spring Security 使用者認證時的資料載入邏輯。
 */
@Service
@Slf4j // ✅ 啟用 SLF4J log 記錄功能
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("✅ CustomUserDetailsService 初始化完成");
    }

    /**
     * 根據傳入的 username 從資料庫讀取 UserEntity。
     * 若找不到使用者會拋出 UsernameNotFoundException。
     * 並將其轉換為 Spring Security 使用的 UserDetails 物件。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 嘗試載入使用者: {}", username);

        UserEntity user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> {
                    log.warn("❌ 找不到使用者: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("✅ 找到使用者: {}", user.getUsername());

        // 回傳 Spring Security 內建的 User 實作，並指派預設角色 ROLE_USER
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
