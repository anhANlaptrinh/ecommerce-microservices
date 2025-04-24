package com.example.cart_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Bỏ qua các trường không khớp giữa JSON và DTO
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    private Long id;
    private String name;
    private Integer price;
    private Integer priceOld;
    private String img;
    public ProductResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Integer getPriceOld() { return priceOld; }
    public void setPriceOld(Integer priceOld) { this.priceOld = priceOld; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
