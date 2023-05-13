package com.example.demo.controller;

import com.example.demo.EntityDTO.ProductSizeDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

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

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @DeleteMapping("/products/{product_id}")
    public void deleteProduct(@PathVariable Long product_id) {
        productRepository.deleteProductById(product_id);
    }

    @GetMapping("/products/size")
    public ProductSizeDTO getProductsSizeByDate(@RequestParam(name = "date")  @DateTimeFormat(pattern = "dd-MM-yyyy") ZonedDateTime date, @RequestParam(name = "productProperty") ProductProperty productProperty) {
        return productService.getProductSizes(date, productProperty);
    }


    @GetMapping("/products/date")//может, и не надо
    public List<LocalDate> getEmployedDates(@RequestParam(name = "size") int size) {
        List<LocalDate> dates = productService.getEmployedDates(size);
        return dates;
    }


}
