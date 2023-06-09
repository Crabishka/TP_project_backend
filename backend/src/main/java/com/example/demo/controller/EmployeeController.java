package com.example.demo.controller;

import com.example.demo.EntityDTO.OrderDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.ProductProperty;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "EmployeeController", description = "Содержит методы API для работника")
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
    @Operation(summary = "Получение заказа по id", description = "Принимает id заказа")
    public OrderDTO getOrderById(@PathVariable Long order_id) {
        Order order = orderService.getOrderById(order_id);
        return OrderDTO
                .builder()
                .activeOrder(order)
                .phoneNumber(order.getUser().getPhoneNumber())
                .name(order.getUser().getName())
                .build();

    }


    @PutMapping("/employee/orders/approve/{order_id}")
    @Operation(summary = "Утверждение заказа", description = "Принимает id заказа")
    public void approveOrder(@PathVariable Long order_id) {
        orderService.approveOrder(order_id);
    }

    @PutMapping("/employee/orders/finish/{order_id}")
    @Operation(summary = "Завершение заказа", description = "Принимает id заказа")
    public void finishOrder(@PathVariable Long order_id) {
        orderService.finishOrder(order_id);
    }

    @PutMapping("/employee/orders/cancel/{order_id}")
    @Operation(summary = "Отменить заказ", description = "Принимает id заказа")
    public void cancelOrder(@PathVariable Long order_id) {
        orderService.cancelOrder(order_id);
    }


    @PutMapping("/employee/orders/pay/{order_id}")
    @Operation(summary = "Оплатить заказ", description = "Принимает id заказа")
    public void payOrder(@PathVariable Long order_id) {
        orderService.payOrder(order_id);
    }


    @PutMapping("/employee/delete/{product_id}")
    @Operation(summary = "Удалить товар из заказа пользователя")
    public void deleteUserProduct(@RequestParam(name = "order_id") int order_id,
                                  @PathVariable Long product_id,
                                  @RequestParam(name = "size") double size) {
        orderService.deleteProductFromUserOrder(order_id, product_id, size);
    }

    @PostMapping("/employee/change/{product_id}/{order_id}/{size}/{new_size}")
    @Operation(summary = "Поменять размер товар из заказа пользователя")
    public void changeUserProduct(@PathVariable Long order_id,
                                  @PathVariable Long product_id,
                                  @PathVariable double size,
                                  @PathVariable double new_size) {
        orderService.changeProductSizeByOrder(order_id, product_id, size, new_size);
    }


    @PostMapping("/employee/last_orders")
    @Operation(summary = "Получение последних 30ти заказов", description = "Ничего не принимает")
    public List<OrderDTO> GetLastOrders() {
        return orderService.getLastOrders();
    }


}
