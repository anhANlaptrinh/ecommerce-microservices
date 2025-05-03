package com.example.cart_service.service;

import com.example.cart_service.dto.AddItemRequest;
import com.example.cart_service.dto.CartDTO;
import com.example.cart_service.entity.Cart;

public interface CartService {
    CartDTO addItem(Long userId, AddItemRequest req);
    CartDTO getCart(Long userId);
    CartDTO removeItem(Long userId, Long productId);
    void clearCart(Long userId);
    CartDTO decreaseItem(Long userId, Long productId);
}
