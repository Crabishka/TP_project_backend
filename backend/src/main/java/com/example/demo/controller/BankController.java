package com.example.demo.controller;

import gcom.example.demo.entity.Product;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "BankController", description = "Управляет банковской заглушкой")
public class BankController {

    private final OrderService orderService;


    public BankController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/bank/go/{order_id}")
    @Operation(summary = "Перейти на страницу оплаты")
    public String getPage(@PathVariable Long order_id, Model model) {
        int cost = (int) orderService.getOrderById(order_id).getTotalCost();
        List<Product> products = orderService.getOrderById(order_id).getProducts();
        model.addAttribute("orderId", order_id);
        model.addAttribute("cost", cost);
        model.addAttribute("products", products);
        return "bankPage.html";
    }

    @PostMapping("/finish/{order_id}")
    @Operation(summary = "Оплатить заказ")
    public String payOrder(@PathVariable Long order_id){
        orderService.payOrder(order_id);
        return "finish.html";
    }
}
