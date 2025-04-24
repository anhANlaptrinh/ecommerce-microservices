package com.example.auth_service.controller;

import com.example.auth_service.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private com.example.auth_service.service.AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody com.example.auth_service.dto.AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public com.example.auth_service.dto.AuthResponse login(@RequestBody com.example.auth_service.dto.AuthRequest request,
                                                           HttpServletResponse response) {
        com.example.auth_service.dto.AuthResponse authResponse = authService.login(request);

        Cookie cookie = new Cookie("token", authResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(259200);

        response.addCookie(cookie);
        return authResponse;
    }

    @GetMapping("/hello")
    public String helloSecured(HttpServletRequest request) {
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
            return "Chưa đăng nhập hoặc token không hợp lệ!";
        }
        String username = jwtUtil.getUsernameFromToken(token);
        return "Xin chào, " + username + "! Bạn đã xác thực thành công.";
    }
}
