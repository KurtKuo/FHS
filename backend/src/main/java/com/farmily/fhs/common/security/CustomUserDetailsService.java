package com.farmily.fhs.common.security;

import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import com.farmily.fhs.user.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

    @Autowired
    private UserRepository userRepository;

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

        // 從 role int 轉 enum
        Role roleEnum = Role.fromCode(user.getRole());
        if (roleEnum == null) {
            roleEnum = Role.MEMBER; // 預設角色
        }

        // 建立權限字串，必須是 ROLE_ 開頭
        String roleString = "ROLE_" + roleEnum.name();

        return new User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(roleString))
        );
    }
}
