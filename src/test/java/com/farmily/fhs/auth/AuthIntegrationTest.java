// AuthIntegrationTest.java
package com.farmily.fhs.auth;

import com.farmily.fhs.auth.dto.LoginRequest;
import com.farmily.fhs.auth.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void loginAndAccessProtectedEndpoint() {
        // 建立登入請求
        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        // 呼叫登入 API 拿到 token
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/login",
                loginRequest,
                LoginResponse.class
        );

        String token = response.getBody().getToken();

        // 呼叫受保護 API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> protectedResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/hello",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, protectedResponse.getStatusCode());
    }
}