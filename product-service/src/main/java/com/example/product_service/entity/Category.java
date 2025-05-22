package com.example.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    private Long id;
    private String name;
    private String img;

    public Category() {} // constructor mặc định

    public Category(Long id, String name, String img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
