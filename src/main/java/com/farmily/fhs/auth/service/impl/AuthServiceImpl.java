package com.farmily.fhs.auth.service.impl;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.auth.service.AuthService;
import com.farmily.fhs.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // ✅ 啟用 SLF4J 日誌功能
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * 用戶登入流程：
     * 1. 驗證帳號密碼
     * 2. 設定 SecurityContext
     * 3. 回傳 JWT token
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("🔐 嘗試登入：{}", request.getUsername());

        // 進行使用者驗證（帳號 + 密碼）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        log.info("✅ 登入成功：{}", request.getUsername());

        // 設定認證資訊到 SecurityContext 中（讓後續可以取用該用戶資訊）
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 產生 JWT Token
        String jwtToken = jwtService.generateToken(authentication);
        log.debug("🧾 產生 JWT token：{}", jwtToken);

        // 回傳包含 token 的登入回應
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
