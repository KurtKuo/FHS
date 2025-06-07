package com.farmily.fhs.auth.service;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.auth.dto.RegisterRequest;
import com.farmily.fhs.auth.dto.RegisterResponse;

/**
 * 授權（認證）相關操作服務介面，例如登入、登出、註冊等。
 */
public interface AuthService {

    /**
     * 使用者登入操作。
     *
     * @param request 包含帳號與密碼的登入請求資料
     * @return 登入回應（含 JWT Token 與使用者名稱）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 使用者登出操作（JWT 模式下為前端丟棄 Token，後端僅作紀錄或擴充用途）。
     *
     * @param token 要登出的 JWT Token
     */
    void logout(String token);

    /**
     * 使用者註冊操作，註冊成功後會立即簽發 JWT Token。
     *
     * @param request 註冊請求資料（帳號、密碼、Email 等）
     * @return 註冊回應（含 JWT Token 與使用者名稱）
     */
    RegisterResponse register(RegisterRequest request);
}
