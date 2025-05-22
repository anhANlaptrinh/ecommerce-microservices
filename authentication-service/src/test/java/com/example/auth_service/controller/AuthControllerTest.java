package com.example.auth_service.controller;

import com.example.auth_service.dto.AuthRequest;
import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import jakarta.servlet.http.Cookie;
import com.example.auth_service.entity.User;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testRegister() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        when(authService.register(any(AuthRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Đăng ký thành công")));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đăng ký thành công"));
    }

    @Test
    public void testLogin() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        AuthResponse response = new AuthResponse();
        response.setToken("fake-jwt-token");

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    public void testHelloSecured_withValidToken() throws Exception {
        String fakeToken = "valid-token";
        String username = "testuser";

        // Tạo User giả
        User user = new User();
        user.setId(123L);
        user.setUsername(username);

        // Mock JWT + AuthService
        when(jwtUtil.validateToken(fakeToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(fakeToken)).thenReturn(username);
        when(authService.getUserByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/api/auth/hello")
                        .cookie(new Cookie("token", fakeToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userId").value("123"));
    }
}
