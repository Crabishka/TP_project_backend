package controller;

import entity.Order;
import entity.ProductProperty;
import org.springframework.web.bind.annotation.*;
import service.OrderService;
import service.ProductService;
import service.UserService;

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
    public Order getOrderById(@PathVariable Long OrderId) {
        Order order = orderService.getOrderById(OrderId);
        return order;
    }

    @GetMapping("/employee/orders")
    public List<ProductProperty> getAllProducts() throws Exception {
        return productService.getAllProductsProperty();
    }

    @PutMapping("/employee/orders/approve/{user_id}")
    public void approveOrder(@PathVariable Long userId) {
        orderService.approveOrder(userId);
    }

    @PutMapping("employee/orders/finish/{user_id} ")
    public void finishOrder(@PathVariable Long userId) {
        orderService.finishOrder(userId);
    }


}