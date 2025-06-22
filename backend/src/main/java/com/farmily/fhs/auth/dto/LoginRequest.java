package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 使用者登入請求物件
 * 繼承自 BaseRequest，包含登入所需的基本欄位
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest extends BaseRequest {

    /**
     * 使用者帳號名稱
     */
    private String username;

    /**
     * 使用者密碼
     */
    private String password;
}