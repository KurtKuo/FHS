package com.farmily.fhs.user.dto;

import com.farmily.fhs.common.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest extends BaseRequest {
    private String currentPassword;
    private String newPassword;
}