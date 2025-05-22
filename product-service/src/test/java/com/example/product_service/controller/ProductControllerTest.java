package com.example.product_service.controller;

import com.example.product_service.dto.ProductDTO;
import com.example.product_service.entity.Category;
import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    public void testGetAllProducts() throws Exception {
        ProductDTO p = new ProductDTO(1L, "Laptop", 1000, 1200, 1L, "Dell", "img.jpg", "desc");
        when(productService.getAllProducts()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetProductById() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000);
        product.setPriceOld(1200);
        product.setBrand("Dell");
        product.setImg("img.jpg");
        product.setDescription("desc");
        product.setCategory(new Category(1L, "Laptop", "img1"));

        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value(1));
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product input = new Product();
        input.setName("Phone");
        input.setPrice(500);
        input.setCategory(new Category(2L, "Phone", "img"));

        Product saved = new Product();
        saved.setId(10L);
        saved.setName("Phone");
        saved.setPrice(500);
        saved.setCategory(new Category(2L, "Phone", "img"));

        ProductDTO dto = new ProductDTO(10L, "Phone", 500, null, 2L, null, null, null);

        when(productService.saveProduct(any(Product.class))).thenReturn(saved);
        when(productService.convertToDTO(saved)).thenReturn(dto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Laptop");
        existing.setPrice(1000);
        existing.setCategory(new Category(1L, "Laptop", "img"));

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("New Laptop");
        updated.setPrice(1200);
        updated.setCategory(new Category(1L, "Laptop", "img"));

        ProductDTO updatedDto = new ProductDTO(
                1L, "New Laptop", 1200, null, 1L, null, null, null
        );

        when(productService.getProductById(1L)).thenReturn(existing);
        when(productService.saveProduct(any(Product.class))).thenReturn(updated);
        when(productService.convertToDTO(updated)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Laptop"))
                .andExpect(jsonPath("$.price").value(1200));
    }

    @Test
    public void testFilterProducts() throws Exception {
        ProductDTO product1 = new ProductDTO(1L, "Filtered Product", 500, null, 2L, "BrandX", "img", "desc");

        when(productService.filterProducts(
                List.of("BrandX"), List.of(2L), 100, 1000
        )).thenReturn(List.of(product1));

        mockMvc.perform(get("/api/products/filter")
                        .param("brands", "BrandX")
                        .param("categories", "2")
                        .param("minPrice", "100")
                        .param("maxPrice", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Filtered Product"));
    }
}
