package com.farmily.fhs.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Slf4j // ✅ 開啟日誌功能
public class AdminController {

    /**
     * 只有具備 ADMIN 權限的使用者可以存取這個後台儀表板
     * @return 歡迎訊息
     */
    @GetMapping("/dashboard")
    public String getAdminDashboard() {
        log.info("🛡️ 管理員嘗試存取後台 /dashboard");
        return "Welcome Admin!";
    }
}
