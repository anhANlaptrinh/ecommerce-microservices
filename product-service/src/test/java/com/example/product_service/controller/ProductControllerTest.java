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

    private static final String NAME_LAPTOP = "Laptop";
    private static final String NAME_PHONE = "Phone";
    private static final String NAME_NEW_LAPTOP = "New Laptop";
    private static final String BRAND_X = "BrandX";
    private static final String BRAND_DELL = "Dell";
    private static final String DESC = "desc";
    private static final String IMG = "img.jpg";
    private static final String FILTERED_NAME = "Filtered Product";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    public void testGetAllProducts() throws Exception {
        ProductDTO p = new ProductDTO(1L, NAME_LAPTOP, 1000, 1200, 1L, BRAND_DELL, IMG, DESC);
        when(productService.getAllProducts()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetProductById() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName(NAME_LAPTOP);
        product.setPrice(1000);
        product.setPriceOld(1200);
        product.setBrand(BRAND_DELL);
        product.setImg(IMG);
        product.setDescription(DESC);
        product.setCategory(new Category(1L, NAME_LAPTOP, "img1"));

        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME_LAPTOP))
                .andExpect(jsonPath("$.category").value(1));
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product input = new Product();
        input.setName(NAME_PHONE);
        input.setPrice(500);
        input.setCategory(new Category(2L, NAME_PHONE, "img"));

        Product saved = new Product();
        saved.setId(10L);
        saved.setName(NAME_PHONE);
        saved.setPrice(500);
        saved.setCategory(new Category(2L, NAME_PHONE, "img"));

        ProductDTO dto = new ProductDTO(10L, NAME_PHONE, 500, null, 2L, null, null, null);

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
        existing.setCategory(new Category(1L, NAME_LAPTOP, "img"));

        Product updated = new Product();
        updated.setId(1L);
        updated.setName(NAME_NEW_LAPTOP);
        updated.setPrice(1200);
        updated.setCategory(new Category(1L, NAME_LAPTOP, "img"));

        ProductDTO updatedDto = new ProductDTO(
                1L, NAME_NEW_LAPTOP, 1200, null, 1L, null, null, null
        );

        when(productService.getProductById(1L)).thenReturn(existing);
        when(productService.saveProduct(any(Product.class))).thenReturn(updated);
        when(productService.convertToDTO(updated)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME_NEW_LAPTOP))
                .andExpect(jsonPath("$.price").value(1200));
    }

    @Test
    public void testFilterProducts() throws Exception {
        ProductDTO product1 = new ProductDTO(1L, FILTERED_NAME, 500, null, 2L, BRAND_X, "img", DESC);

        when(productService.filterProducts(
                List.of(BRAND_X), List.of(2L), 100, 1000
        )).thenReturn(List.of(product1));

        mockMvc.perform(get("/api/products/filter")
                        .param("brands", BRAND_X)
                        .param("categories", "2")
                        .param("minPrice", "100")
                        .param("maxPrice", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(FILTERED_NAME));
    }
}
