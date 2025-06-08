package com.farmily.fhs.user.controller;

import com.farmily.fhs.user.dto.ChangePasswordRequest;
import com.farmily.fhs.user.dto.ChangePasswordResponse;
import com.farmily.fhs.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@Slf4j // 啟用 Lombok SLF4J 日誌功能
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 取得使用者個人資料（需登入）
     * @param authentication Spring Security 自動注入的使用者認證資訊
     * @return ResponseEntity 包含歡迎訊息與 HTTP 200 OK
     */
    @GetMapping("/user/profile")
    public ResponseEntity<String> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("👤 存取個人資料：{}", username);

        String message = "Hello, " + username + "! This is your profile.";
        return ResponseEntity.ok(message);
    }

    /**
     * 刪除目前登入使用者的帳號
     * @param authentication Spring Security 自動注入的使用者認證資訊
     * @return ResponseEntity 包含刪除成功訊息與 HTTP 200 OK
     */
    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        String username = authentication.getName(); // 從 token 解出目前登入使用者
        log.info("🗑️ 使用者請求刪除帳號：{}", username);

        userService.deleteUser(username);

        log.info("✅ 帳號已刪除：{}", username);
        return ResponseEntity.ok("帳號已刪除");
    }

    /**
     * 更改密碼（需登入）
     * @param request 包含原密碼與新密碼
     * @param authentication Spring Security 的使用者資訊
     * @return ResponseEntity 包含成功訊息與 HTTP 200 OK
     */
    @PutMapping("/user/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        log.info("🔐 {} 嘗試更改密碼", username);

        userService.changePassword(username, request);

        ChangePasswordResponse response = new ChangePasswordResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("密碼已成功更新");
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
