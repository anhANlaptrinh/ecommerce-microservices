package com.example.cart_service.dto;

import java.util.List;

public class CartDTO {
    private List<CartItemDTO> items;
    private Integer totalAmount;

    // getters & setters
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
}
