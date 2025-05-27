package com.farmily.fhs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 所有 request DTO 的基底類別。
 * 提供統一的請求欄位（可擴充）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {

    /**
     * 用於追蹤 request 的唯一識別碼，可由前端產生或 server 記錄
     */
    private String requestId;

    /**
     * 客戶端版本資訊（例如 App 版本號）
     */
    private String clientVersion;

    /**
     * 語系設定，如 zh-TW、en-US，用於多語系支援
     */
    private String locale;

    /**
     * 請求的建立時間，預設為當下時間
     */
    private LocalDateTime timestamp = LocalDateTime.now();
}
