// src/main/java/com/example/product_service/repository/ProductRepository.java
package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE 
          (:brands IS NULL OR p.brand        IN :brands)
          AND (:cats   IS NULL OR p.category.id IN :cats)
          AND (:minP   IS NULL OR p.price >= :minP)
          AND (:maxP   IS NULL OR p.price <= :maxP)
    """)
    List<Product> filterProducts(
            @Param("brands") List<String> brands,
            @Param("cats")   List<Long>   categoryIds,
            @Param("minP")   Integer      minPrice,
            @Param("maxP")   Integer      maxPrice
    );

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();
}
