package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 使用者登入回應物件
 * 繼承自 BaseResponse，封裝登入成功後回傳的資訊
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends BaseResponse {

    /**
     * JWT 權杖，用於後續驗證請求
     */
    private String token;

    /**
     * 使用者名稱
     */
    private String username;
}