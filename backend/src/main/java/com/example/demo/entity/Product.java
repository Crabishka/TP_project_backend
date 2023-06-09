package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne

    private ProductProperty productProperty;

    @Column(name = "size")
    private double size;


    @ManyToMany()
    @JsonManagedReference
    private List<Order> orders;

    public Product(int id, ProductProperty productProperty, double size) {
        this.id = id;
        this.productProperty = productProperty;
        this.size = size;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductProperty getProductProperty() {
        return productProperty;
    }

    public void setProductProperty(ProductProperty productProperty) {
        this.productProperty = productProperty;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
