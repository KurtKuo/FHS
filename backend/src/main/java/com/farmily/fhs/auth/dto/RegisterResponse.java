package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 使用者註冊回應物件
 * 繼承自 BaseResponse，回傳註冊成功後的資訊
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegisterResponse extends BaseResponse {

    /**
     * JWT 權杖，用於後續驗證請求
     */
    private String token;

    /**
     * 註冊成功的使用者名稱
     */
    private String username;
}