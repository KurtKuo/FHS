package com.farmily.fhs.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Slf4j // ✅ 啟用日誌功能
public class AdminController {

    /**
     * 只有具備 ADMIN 權限的使用者可以存取後台儀表板
     * @return ResponseEntity 包含歡迎訊息與 HTTP 200 OK
     */
    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        log.info("🛡️ 管理員嘗試存取後台 /dashboard");
        return ResponseEntity.ok("Welcome Admin!");
    }
}
