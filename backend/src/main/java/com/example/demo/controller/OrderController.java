package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Tag(name = "OrderController", description = "Управляет заказами")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Получить продукт", description = "Принимает id продукта")
    public Order getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return order;
    }

    @PutMapping("/orders")
    @Operation(summary = "Изменить заказ", description = "Принимает Order")
    public void updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
    }

    @PostMapping("/orders")
    @Operation(summary = "Создание заказа", description = "Принимает Order")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order.getOrderTime(), order.getProducts(), order.getUser());
    }






}