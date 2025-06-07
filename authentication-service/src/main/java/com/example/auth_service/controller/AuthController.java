package com.example.auth_service.controller;

import com.example.auth_service.entity.User;
import com.example.auth_service.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private com.example.auth_service.service.AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody com.example.auth_service.dto.AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public com.example.auth_service.dto.AuthResponse login(@RequestBody com.example.auth_service.dto.AuthRequest request,
                                                           HttpServletResponse response) {
        com.example.auth_service.dto.AuthResponse authResponse = authService.login(request);

        Cookie cookie = new Cookie("token", authResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(259200);
        cookie.setDomain("myjenkins.click");
        response.addCookie(cookie);

        response.setHeader("Set-Cookie", String.format(
                "token=%s; Path=/; Max-Age=259200; HttpOnly; Secure; SameSite=None; Domain=myjenkins.click",
                authResponse.getToken()
        ));
        return authResponse;
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> helloSecured(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null || !jwtUtil.validateToken(token)) {
            Cookie cookie = new Cookie("token", null);
            cookie.setPath("/");
            cookie.setMaxAge(0); // Xóa cookie
            response.addCookie(cookie);
            return ResponseEntity.status(401).body(Map.of("message", "Chưa đăng nhập hoặc token không hợp lệ!"));
        }
        String username = jwtUtil.getUsernameFromToken(token);
        User user = authService.getUserByUsername(username);
        return ResponseEntity.ok(Map.of(
                "username", username,
                "userId", user.getId().toString()
        ));    }
}