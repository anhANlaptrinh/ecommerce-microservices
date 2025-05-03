package com.example.cart_service.repository;

import com.example.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteByCartId(Long cartId);
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
