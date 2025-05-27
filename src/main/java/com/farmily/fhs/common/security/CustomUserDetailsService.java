package com.farmily.fhs.common.security;

import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根據傳入的 username 從資料庫讀取 UserEntity。
     * 找不到使用者會拋出 UsernameNotFoundException。
     * 並將 UserEntity 包裝成 SecurityUser (實作 UserDetails) 回傳給 Spring Security 使用。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("使用者不存在: " + username));
        return new SecurityUser(user);
    }
}
