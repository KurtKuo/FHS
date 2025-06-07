package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // 加這個
public class RegisterRequest extends BaseRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
}
