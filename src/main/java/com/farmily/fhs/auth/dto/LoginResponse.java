package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends BaseResponse {
    private String token;
    private String username;
}
