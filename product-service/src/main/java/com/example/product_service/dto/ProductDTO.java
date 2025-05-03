// src/main/java/com/example/product_service/dto/ProductDTO.java
package com.example.product_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private Long id;
    private String name;
    private int price;

    @JsonProperty("price_old")
    private Integer priceOld;

    private Long category;
    private String brand;
    private String img;
    private String description;

    public ProductDTO(Long id, String name, int price, Integer priceOld,
                      Long category, String brand,
                      String img, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.priceOld = priceOld;
        this.category = category;
        this.brand = brand;
        this.img = img;
        this.description = description;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public Integer getPriceOld() { return priceOld; }
    public void setPriceOld(Integer priceOld) { this.priceOld = priceOld; }
    public Long getCategory() { return category; }
    public void setCategoryName(Long category) { this.category = category; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
