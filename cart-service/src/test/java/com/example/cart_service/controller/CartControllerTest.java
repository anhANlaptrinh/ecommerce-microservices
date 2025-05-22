package com.example.cart_service.controller;

import com.example.cart_service.config.TestSecurityConfig;
import com.example.cart_service.dto.AddItemRequest;
import com.example.cart_service.dto.CartDTO;
import com.example.cart_service.dto.CartItemDTO;
import com.example.cart_service.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CartController.class)
@Import(TestSecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 123L;

    private CartDTO mockCartDTO() {
        CartItemDTO item = new CartItemDTO();
        item.setProductId(1L);
        item.setName("Sample Product");
        item.setImg("image.jpg");
        item.setQuantity(2);
        item.setUnitPrice(100);
        item.setSubtotal(200);

        CartDTO cart = new CartDTO();
        cart.setItems(Collections.singletonList(item));
        cart.setTotalAmount(200);

        return cart;
    }

    @Test
    public void testAddItem() throws Exception {
        AddItemRequest req = new AddItemRequest();
        req.setProductId(1L);
        req.setQuantity(2);

        when(cartService.addItem(eq(userId), any(AddItemRequest.class)))
                .thenReturn(mockCartDTO());

        mockMvc.perform(post("/api/cart/items")
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(200));
    }

    @Test
    public void testGetCart() throws Exception {
        when(cartService.getCart(userId)).thenReturn(mockCartDTO());

        mockMvc.perform(get("/api/cart")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(200));
    }

    @Test
    public void testRemoveItem() throws Exception {
        when(cartService.removeItem(userId, 1L)).thenReturn(mockCartDTO());

        mockMvc.perform(delete("/api/cart/items/1")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(200));
    }

    @Test
    public void testClearCart() throws Exception {
        mockMvc.perform(delete("/api/cart")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testDecreaseItem() throws Exception {
        when(cartService.decreaseItem(userId, 1L)).thenReturn(mockCartDTO());

        mockMvc.perform(put("/api/cart/items/1/decrease")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(200));
    }
}
