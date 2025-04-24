package com.example.cart_service.service;

import com.example.cart_service.dto.AddItemRequest;
import com.example.cart_service.dto.CartDTO;

public interface CartService {
    CartDTO addItem(String cartId, AddItemRequest req, Long userId);
    CartDTO getCart(String cartId, Long userId);
    CartDTO mergeCart(String guestCartId, Long userId);
}
