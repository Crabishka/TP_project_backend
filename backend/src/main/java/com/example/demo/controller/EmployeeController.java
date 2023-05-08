package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.ProductProperty;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    private final UserService userService;
    private final OrderService orderService;

    private final ProductService productService;

    public EmployeeController(UserService userService, OrderService orderService, ProductService productService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/employee/orders/{order_id}")
    public Order getOrderById(@PathVariable Long order_id) {
        Order order = orderService.getOrderById(order_id);
        return order;
    }

    @GetMapping("/employee/orders")
    public List<ProductProperty> getAllProducts() throws Exception {
        return productService.getAllProductsProperty();
    }

    @PutMapping("/employee/orders/approve/{user_id}")
    public void approveOrder(@PathVariable Long user_id) {
        orderService.approveOrder(user_id);
    }

    @PutMapping("employee/orders/finish/{user_id} ")
    public void finishOrder(@PathVariable Long user_id) {
        orderService.finishOrder(user_id);
    }


}
