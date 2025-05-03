package com.example.cart_service.controller;

import com.example.cart_service.dto.*;
import com.example.cart_service.entity.Cart;
import com.example.cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** Thêm item vào giỏ */
    @PostMapping("/items")
    public CartDTO addItem(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody @Valid AddItemRequest req
    ) {
        return cartService.addItem(userId, req);
    }

    /** Lấy giỏ hàng của người dùng */
    @GetMapping
    public CartDTO getCart(@RequestHeader("X-USER-ID") Long userId) {
        return cartService.getCart(userId);
    }

    /** Xóa item khỏi giỏ hàng */
    @DeleteMapping("/items/{productId}")
    public CartDTO removeItem(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long productId
    ) {
        return cartService.removeItem(userId, productId);
    }

    /** Xóa toàn bộ giỏ hàng */
    @DeleteMapping
    public void clearCart(@RequestHeader("X-USER-ID") Long userId) {
        cartService.clearCart(userId);
    }

    @PutMapping("/items/{productId}/decrease")
    public CartDTO decreaseItem(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long productId
    ) {
        return cartService.decreaseItem(userId, productId);
    }
}
