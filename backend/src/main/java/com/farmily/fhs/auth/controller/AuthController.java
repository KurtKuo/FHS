package com.farmily.fhs.auth.controller;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.auth.dto.RegisterRequest;
import com.farmily.fhs.auth.dto.RegisterResponse;
import com.farmily.fhs.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Slf4j // 啟用 Lombok SLF4J 日誌功能
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 使用者註冊帳號
     * @param request 請求物件，包含帳號、密碼、Email、電話等
     * @return ResponseEntity 包含註冊回應 DTO 與 HTTP 狀態碼（200 OK）
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        log.info("📝 註冊新帳號：{}", request.getUsername());
        RegisterResponse response = authService.register(request);

        // 補充 response 裡狀態與訊息
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Register success");
        response.setTimestamp(LocalDateTime.now());

        log.info("✅ 註冊成功，自動登入使用者：{}", response.getUsername());
        // 回傳 HTTP 201 Created 並附帶回應 DTO
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 使用者登入端點
     * @param request 請求物件，包含帳號與密碼
     * @return ResponseEntity 包含登入回應 DTO 與 HTTP 200 OK
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("📩 收到登入請求：{}", request.getUsername());
        LoginResponse response = authService.login(request);

        // 補充 response 裡狀態與訊息
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Login success");
        response.setTimestamp(LocalDateTime.now());

        log.info("✅ 登入成功，簽發 JWT 給使用者：{}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * 使用者登出端點（JWT 模式下，前端通常只需丟棄 token）
     * @param token HTTP Header 的 Authorization 欄位 (Bearer Token)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("🚪 登出請求，Token：{}", token);
        authService.logout(token);
        log.info("✅ 登出成功（JWT 模式下前端自行清除）");
        // 登出不需回傳資料，HTTP 204 No Content
        return ResponseEntity.noContent().build();
    }
}
