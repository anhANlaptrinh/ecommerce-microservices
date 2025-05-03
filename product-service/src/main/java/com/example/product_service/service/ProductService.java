package com.example.product_service.service;

import com.example.product_service.dto.ProductDTO;
import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductService(ProductRepository repo) {
        this.productRepository = repo;
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAllWithCategory().stream()
                .map(p -> new ProductDTO(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getPriceOld(),
                        p.getCategory().getId(),
                        p.getBrand(),
                        p.getImg(),
                        p.getDescription()
                ))
                .toList();
    }
    // Lấy tất cả sản phẩm và chuyển thành ProductDTO
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Phương thức lọc sản phẩm theo tham số
    public List<ProductDTO> filterProducts(List<String> brands, List<Long> categories, Integer minPrice, Integer maxPrice) {
        List<Product> products = productRepository.filterProducts(brands, categories, minPrice, maxPrice);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Chuyển đổi từ entity sang DTO
    public ProductDTO convertToDTO(Product product) {
        Long categoryId = (product.getCategory() != null) ? product.getCategory().getId() : null;
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getPriceOld(),
                categoryId,
                product.getBrand(),
                product.getImg(),
                product.getDescription()
        );
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
