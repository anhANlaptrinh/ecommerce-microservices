package com.example.product_service.controller;

import com.example.product_service.entity.Category;
import com.example.product_service.service.CategoryService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void testGetAllCategories() throws Exception {
        Category c1 = new Category(1L, "Laptop", "img1");
        Category c2 = new Category(2L, "Phone", "img2");

        when(categoryService.getAllCategories()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetCategoryById() throws Exception {
        Category c = new Category(1L, "Laptop", "img1");
        when(categoryService.getCategoryById(1L)).thenReturn(c);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    public void testCreateCategory() throws Exception {
        Category c = new Category(null, "Phone", "img1");
        Category saved = new Category(10L, "Phone", "img1");

        when(categoryService.saveCategory(any(Category.class))).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        Category existing = new Category(5L, "OldName", "oldImg");
        Category updated = new Category(5L, "NewName", "newImg");

        when(categoryService.getCategoryById(5L)).thenReturn(existing);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/5"))
                .andExpect(status().isNoContent());
    }
}
