package com.example.product_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;

    @Column(name = "price_old")
    private int priceOld;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;

    private String brand;
    private String img;

    @Column(columnDefinition = "TEXT")
    private String description;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getPriceOld() { return priceOld; }
    public void setPriceOld(int priceOld) { this.priceOld = priceOld; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
