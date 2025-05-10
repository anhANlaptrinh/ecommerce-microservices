package com.example.product_service.controller;

import com.example.product_service.dto.ProductDTO;
import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductDTO>> filterProducts(
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice) {
        return ResponseEntity.ok(productService.filterProducts(brands, categories, minPrice, maxPrice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getPriceOld(),
                product.getCategory().getId(),
                product.getBrand(),
                product.getImg(),
                product.getDescription()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
        Product saved = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.convertToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product product = productService.getProductById(id);
        product.setId(updatedProduct.getId());
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setPriceOld(updatedProduct.getPriceOld());
        product.setCategory(updatedProduct.getCategory());
        product.setBrand(updatedProduct.getBrand());
        product.setImg(updatedProduct.getImg());
        product.setDescription(updatedProduct.getDescription());
        Product updated = productService.saveProduct(product);
        return ResponseEntity.ok(productService.convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
