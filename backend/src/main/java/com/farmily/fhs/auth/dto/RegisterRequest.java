package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 使用者註冊請求物件
 * 繼承自 BaseRequest，包含註冊時所需的使用者資訊
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest extends BaseRequest {

    /**
     * 使用者帳號名稱
     */
    private String username;

    /**
     * 使用者密碼
     */
    private String password;

    /**
     * 使用者電子郵件
     */
    private String email;

    /**
     * 使用者手機號碼
     */
    private String phone;
}