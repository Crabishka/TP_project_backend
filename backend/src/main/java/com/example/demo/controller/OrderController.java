package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return order;
    }


    @PutMapping("/orders")
    public void updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
    }

    @PostMapping("/orders")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order.getOrderTime(), order.getProducts(), order.getUser());
    }






}
