package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    private final UserService userService;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final ProductService productService;

    private final UserRepository userRepository;

    public ProductController(UserService userService, OrderService orderService, ProductRepository productRepository, ProductService productService, UserRepository userRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @PostMapping("/product")
    public Product createOrder(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/product/cancel/{product_id}")
    public void cancelActiveOrder(@PathVariable Long product_id) {
        productRepository.deleteProductById(product_id);
    }
}
