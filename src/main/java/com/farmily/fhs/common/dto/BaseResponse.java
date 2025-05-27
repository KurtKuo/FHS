package com.farmily.fhs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 所有 response DTO 的基底類別。
 * 提供統一的 API 回應格式。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    /**
     * 回應狀態碼，例如 200 表示成功，400/500 表示錯誤
     */
    private Integer status;

    /**
     * 回應訊息，例如成功訊息或錯誤描述
     */
    private String message;

    /**
     * 回應產生時間，預設為當下時間
     */
    private LocalDateTime timestamp = LocalDateTime.now();
}
