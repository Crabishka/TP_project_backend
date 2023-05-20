package com.example.demo.controller;

import com.example.demo.EntityDTO.ProductSizeDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@Tag(name = "ProductController", description = "Управляет продуктами")
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
    @Operation(summary = "Создать продукт", description = "Принимает Product")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @DeleteMapping("/products/{product_id}")
    @Operation(summary = "Удалить продукт", description = "Принимает id продукта")
    public void deleteProduct(@PathVariable Long product_id) {
        productRepository.deleteProductById(product_id);
    }

    @GetMapping("/products/size")
    @Operation(summary = "Получить список имеющихся размеров продукта на указанную дату", description = "Принимает дату в формате \"dd-MM-yyyy\" и ProductProperty")
    public ProductSizeDTO getProductsSizeByDate(@RequestParam(name = "date")  @DateTimeFormat(pattern = "dd-MM-yyyy") ZonedDateTime date, @RequestParam(name = "productProperty") ProductProperty productProperty) {
        return productService.getProductSizes(date, productProperty);
    }


    @GetMapping("/products/date")//может, и не надо
    public List<ZonedDateTime> getEmployedDates(@RequestParam(name = "size") int size) {
        List<ZonedDateTime> dates = productService.getEmployedDates(size);
        return dates;
    }


}
