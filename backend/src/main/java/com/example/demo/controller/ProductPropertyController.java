package com.example.demo.controller;

import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "ProductPropertyController", description = "Управляет описанием товаров")
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

    @PostMapping("/products_property")
    @Operation(summary = "Создать свойства продукта", description = "Принимает ProductProperty")
    public ProductProperty createProductProperty(@RequestBody ProductProperty productProperty) {
        return productService.createProductProperty(productProperty);
    }

    @GetMapping("/products_property")
    @Operation(summary = "Получить свойства всех продуктов", description = "Ничего не принимает")
    public List<ProductProperty> getAllProductsProperty() throws Exception {
        return productService.getAllProductsProperty();
    }

    @DeleteMapping("/products_property/{product_property_id}")
    @Operation(summary = "Удалить свойства продукта", description = "Принимает id свойства")
    public void deleteProductProperty(@PathVariable Long product_property_id) {
        productPropertiesRepository.deleteProductPropertyById(product_property_id);
    }


    @GetMapping("/products_property/{id}")
    @Operation(summary = "Получить Свойства продукта по его id", description = "Принимает id продукта")
    public ProductProperty getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

}
