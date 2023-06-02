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

    List<Product> findAllByProductPropertyIdAndIdNotIn(Long id, List<Integer> ids);

    List<Product> findAllByProductPropertyIdAndIdNotInAndSize(Long id, List<Integer> ids, double size);
    List<Product> findAllByIdNotIn( List<Integer> ids);

    void deleteProductById(Long id);

}
