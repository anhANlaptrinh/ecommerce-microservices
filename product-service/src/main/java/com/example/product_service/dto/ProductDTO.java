// src/main/java/com/example/product_service/dto/ProductDTO.java
package com.example.product_service.dto;

public class ProductDTO {
    private Long id;
    private String name;
    private int price;
    private int priceOld;
    private String categoryName;
    private String brand;
    private String img;
    private String description;

    public ProductDTO() {}

    public ProductDTO(Long id, String name, int price, int priceOld,
                      String categoryName, String brand,
                      String img, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.priceOld = priceOld;
        this.categoryName = categoryName;
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
    public int getPriceOld() { return priceOld; }
    public void setPriceOld(int priceOld) { this.priceOld = priceOld; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
