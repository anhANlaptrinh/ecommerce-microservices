package com.example.cart_service.service;

import com.example.cart_service.dto.*;
import com.example.cart_service.entity.*;
import com.example.cart_service.exception.CartNotFoundException;
import com.example.cart_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final String PRODUCT_URL = "http://product-service.product-service:8081/";

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final RestTemplate restTemplate;

    public CartServiceImpl(
            CartRepository cartRepo,
            CartItemRepository itemRepo,
            RestTemplate restTemplate) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public CartDTO addItem(Long userId, AddItemRequest req) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setCartId(UUID.randomUUID().toString());
                    return cartRepo.save(c);
                });

        ProductResponse prod = restTemplate.getForObject(
                PRODUCT_URL + req.getProductId(),
                ProductResponse.class
        );
        if (prod == null) throw new RuntimeException("Product not found");

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(req.getProductId()))
                .findFirst();

        CartItem item = existing.orElseGet(CartItem::new);
        item.setCart(cart);
        item.setProductId(req.getProductId());
        item.setUnitPrice(prod.getPrice());
        item.setQuantity(
                existing.map(i -> i.getQuantity() + req.getQuantity())
                        .orElse(req.getQuantity())
        );
        if (existing.isEmpty()) cart.getItems().add(item);
        itemRepo.save(item);

        return toDTO(cart);
    }

    @Override
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));
        return toDTO(cart);
    }

    @Transactional
    public CartDTO removeItem(Long userId, Long productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        itemRepo.deleteByCartIdAndProductId(cart.getId(), productId);

        return toDTO(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));
        itemRepo.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepo.save(cart);
    }

    private CartDTO toDTO(Cart cart) {
        List<CartItemDTO> list = cart.getItems().stream()
                .map(i -> {
                    ProductResponse prod = restTemplate.getForObject(
                            PRODUCT_URL + i.getProductId(),
                            ProductResponse.class
                    );
                    if (prod == null) throw new RuntimeException("Product not found");

                    CartItemDTO d = new CartItemDTO();
                    d.setProductId(i.getProductId());
                    d.setName(prod.getName());
                    d.setImg(prod.getImg());
                    d.setUnitPrice(prod.getPrice());
                    d.setQuantity(i.getQuantity());
                    d.setSubtotal(i.getQuantity() * prod.getPrice());
                    return d;
                })
                .collect(Collectors.toList());

        CartDTO dto = new CartDTO();
        dto.setItems(list);
        dto.setTotalAmount(list.stream()
                .mapToInt(CartItemDTO::getSubtotal)
                .sum());
        return dto;
    }

    @Override
    @Transactional
    public CartDTO decreaseItem(Long userId, Long productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        Optional<CartItem> optionalItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();

        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Item not found in cart");
        }

        CartItem item = optionalItem.get();

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            itemRepo.save(item);
        } else {
            // nếu quantity = 1, thì xóa item
            cart.getItems().remove(item);
            itemRepo.deleteByCartIdAndProductId(cart.getId(), productId);
        }

        return toDTO(cart);
    }
}
