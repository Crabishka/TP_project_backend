package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product getFirstBySizeAndProductPropertyId(double size, Long productPropertyId);
    @Query("SELECT DISTINCT p.size FROM Product p where p.productProperty.id = ?1")
    List<Double> findDistinctSize(Long id);

    List<Product> findAllByProductPropertyId(Long id);

    void deleteProductById(Long id);

}
