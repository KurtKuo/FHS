package com.farmily.fhs.auth.service.impl;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.auth.dto.RegisterRequest;
import com.farmily.fhs.auth.dto.RegisterResponse;
import com.farmily.fhs.auth.service.AuthService;
import com.farmily.fhs.common.repository.UserRepository;
import com.farmily.fhs.common.repository.entity.UserEntity;
import com.farmily.fhs.common.security.JwtService;
import com.farmily.fhs.user.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j // ✅ 啟用 SLF4J 日誌功能
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("使用者名稱已存在！");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.MEMBER.getCode()) // 註冊皆是以 會員註冊
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("❌ 註冊失敗：{}", e.getMessage(), e);
            throw new RuntimeException("註冊失敗，請稍後再試！");
        }

        String token = jwtService.generateToken(user.getUsername());

        RegisterResponse response = RegisterResponse.builder()
                .username(user.getUsername())
                .token(token)
                .build();

        response.setStatus(200);
        response.setMessage("SUCCESS");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * 用戶登入流程：
     * 1. 驗證帳號密碼
     * 2. 設定 SecurityContext
     * 3. 回傳 JWT token
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtService.generateToken(authentication);
        return new LoginResponse(jwtToken, request.getUsername());
    }

    /**
     * 用戶登出流程（目前為 JWT，實際登出由前端處理，僅示意）
     */
    @Override
    public void logout(String token) {
        log.info("🚪 使用者登出（實際由前端丟棄 JWT）：{}", token);
        // 若改為有狀態登入（如 Session），可在這裡實作清除操作
    }
}
