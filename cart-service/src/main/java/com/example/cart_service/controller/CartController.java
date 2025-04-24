package com.example.cart_service.controller;

import com.example.cart_service.dto.*;
import com.example.cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
            @RequestHeader(value = "X-CART-ID", required = false) String cartId,
            @RequestBody @Valid AddItemRequest req
    ) {
        if (cartId == null) {
            cartId = UUID.randomUUID().toString();
        }
        Long userId = null; // TODO: parse từ JWT
        return cartService.addItem(cartId, req, userId);
    }

    /** Lấy giỏ */
    @GetMapping
    public CartDTO getCart(
            @RequestHeader("X-CART-ID") String cartId
    ) {
        Long userId = null; // TODO: parse từ JWT
        return cartService.getCart(cartId, userId);
    }

    /** Merge guest → user */
    @PostMapping("/merge")
    public CartDTO mergeCart(
            @RequestHeader("X-CART-ID") String guestCartId,
            @RequestParam Long userId
    ) {
        return cartService.mergeCart(guestCartId, userId);
    }
}
