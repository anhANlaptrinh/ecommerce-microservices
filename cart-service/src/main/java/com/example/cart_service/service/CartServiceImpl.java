package com.example.cart_service.service;

import com.example.cart_service.dto.*;
import com.example.cart_service.entity.*;
import com.example.cart_service.exception.CartNotFoundException;
import com.example.cart_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final String PRODUCT_URL = "http://localhost:8080/api/products/";

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
    public CartDTO addItem(String cartId, AddItemRequest req, Long userId) {
        Cart cart = cartRepo.findByCartId(cartId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCartId(cartId);
                    c.setUserId(userId);
                    return cartRepo.save(c);
                });

        // Lấy giá từ product-service
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
    public CartDTO getCart(String cartId, Long userId) {
        Cart cart = (userId != null)
                ? cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user"))
                : cartRepo.findByCartId(cartId)
                .orElseThrow(() -> new CartNotFoundException("Guest cart not found"));
        return toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO mergeCart(String guestCartId, Long userId) {
        Cart guest = cartRepo.findByCartId(guestCartId)
                .orElseThrow(() -> new CartNotFoundException("Guest cart not found"));
        Cart user = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCartId(UUID.randomUUID().toString());
                    c.setUserId(userId);
                    return cartRepo.save(c);
                });

        for (CartItem gi : guest.getItems()) {
            user.getItems().stream()
                    .filter(ui -> ui.getProductId().equals(gi.getProductId()))
                    .findFirst()
                    .ifPresentOrElse(
                            ui -> ui.setQuantity(ui.getQuantity() + gi.getQuantity()),
                            () -> {
                                gi.setCart(user);
                                user.getItems().add(gi);
                            }
                    );
        }
        cartRepo.save(user);
        cartRepo.deleteByCartId(guestCartId);
        return toDTO(user);
    }

    private CartDTO toDTO(Cart cart) {
        List<CartItemDTO> list = cart.getItems().stream()
                .map(i -> {
                    // Lấy thêm name/img
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
}
