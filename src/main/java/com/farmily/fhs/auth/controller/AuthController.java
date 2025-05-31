package com.farmily.fhs.auth.controller;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j // ✅ 啟用日誌功能
public class AuthController {

    private final AuthService authService;

    /**
     * 使用者登入端點
     * @param request 帳號 + 密碼
     * @return JWT Token 與使用者名稱
     */
    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("📩 收到登入請求：{}", request.getUsername());
        LoginResponse response = authService.login(request);
        log.info("✅ 登入成功，簽發 JWT 給使用者：{}", response.getUsername());
        return response;
    }

    /**
     * 使用者登出端點（JWT 模式下，前端通常只需丟棄 token）
     * @param token HTTP Header 的 Authorization 欄位
     */
    @PostMapping("/auth/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        log.info("🚪 登出請求，Token：{}", token);
        authService.logout(token);
        log.info("✅ 登出成功（JWT 模式下前端自行清除）");
    }

    /**
     * 取得使用者個人資料（需要登入）
     * @param authentication Spring Security 自動注入的使用者資訊
     * @return 歡迎訊息
     */
    @GetMapping("/user/profile")
    public String getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("👤 存取個人資料：{}", username);
        return "Hello, " + username + "! This is your profile.";
    }
}
