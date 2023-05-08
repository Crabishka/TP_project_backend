package com.example.demo.controller;

import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductPropertyController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final ProductPropertiesRepository productPropertiesRepository;
    private final UserRepository userRepository;

    public ProductPropertyController(UserService userService, OrderService orderService, ProductService productService, ProductPropertiesRepository productPropertiesRepository, UserRepository userRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.productPropertiesRepository = productPropertiesRepository;

        this.userRepository = userRepository;
    }

    @PostMapping("/productProperty")
    public ProductProperty createOrder(@RequestBody ProductProperty productProperty) {
        return productService.createProductProperty(productProperty);
    }

    @PutMapping("/product_property/cancel/{product_property_id}")
    public void cancelActiveOrder(@PathVariable Long product_property_id) {
        productPropertiesRepository.deleteProductPropertyById(product_property_id);
    }
}
