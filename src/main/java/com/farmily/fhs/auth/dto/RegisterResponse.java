package com.farmily.fhs.auth.dto;

import com.farmily.fhs.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegisterResponse extends BaseResponse {
    private String token;
    private String username;
}
