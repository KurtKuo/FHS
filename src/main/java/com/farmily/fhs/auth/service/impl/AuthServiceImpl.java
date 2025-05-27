package com.farmily.fhs.auth.service.impl;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import com.farmily.fhs.auth.service.AuthService;
import com.farmily.fhs.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 這裡你可以改成 JWT 生成，暫時使用 UUID 假 token
        String fakeToken = UUID.randomUUID().toString();

        return new LoginResponse(fakeToken, request.getUsername());
    }

    @Override
    public void logout(String token) {
        // 暫時不實作，因為如果是 JWT，前端只要丟棄 token 即可
        // 如果未來用 session，可在這裡做 session 失效處理
    }
}
