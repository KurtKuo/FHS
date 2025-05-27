package com.farmily.fhs.auth.service;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import org.springframework.stereotype.Service;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
}
