package com.farmily.fhs.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncoderTest {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encoded = "$2a$10$7sPTpVeTxAkk/Lr5uIfdyu/F82/vCAxVQR/.vAZXvZrVGvLlsr5W6";
        System.out.println("Match? " + encoder.matches(rawPassword, encoded));

        PasswordEncoder encoder2 = new BCryptPasswordEncoder();
        String rawPassword2 = "password";
        String hashed = encoder2.encode(rawPassword2);
        System.out.println("✅ Hashed password: " + hashed);
    }
}
