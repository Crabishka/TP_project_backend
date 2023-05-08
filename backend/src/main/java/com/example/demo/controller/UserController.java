package com.example.demo.controller;

import com.example.demo.EntityDTO.ProductSizeDTO;
import com.example.demo.EntityDTO.UserAuthDTO;
import com.example.demo.EntityDTO.UserRegDTO;
import com.example.demo.EntityDTO.JwtResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.ProductProperty;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    private final UserRepository userRepository;

    public UserController(UserService userService, OrderService orderService, ProductService productService, UserRepository userRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @PostMapping("/user")//
    public User createUser(@RequestBody UserRegDTO user) {
        return userRepository.save(user.toUser());
    }

    @GetMapping("/orders/{id}")
    public Order getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return order;
    }

    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable Long id) {
        User user = userRepository.getReferenceById(id);
        return user.getName();
    }

    @GetMapping("/products")//
    public List<ProductProperty> getAllProducts() throws Exception {
        return productService.getAllProductsProperty();
    }

    @GetMapping("/user_orders/{id}")//
    public List<Order> getAllOrders(@PathVariable Long id) throws Exception {
        return orderService.getAllOrders(id);
    }

    @GetMapping("/products/{id}")
    public ProductProperty getProductById(@PathVariable Long id) {
        ProductProperty product = productService.getProductById(id);
        return product;
    }

    @PostMapping("/products/add/{user_id}/{product_id}")
    public void addProductToCart(@PathVariable Long user_id, @PathVariable Long product_id, @RequestParam(name = "size") int size) {
        userService.addProductToCart(user_id, product_id, size);
    }

    @GetMapping("/products/size")
    public ProductSizeDTO getProductsSizeByDate(@RequestParam(name = "date") ZonedDateTime date, @RequestParam(name = "productProperty") ProductProperty productProperty) {
        return productService.getProductSizes(date, productProperty);
    }

    @GetMapping("/products/date")
    public List<LocalDate> getEmployedDates(@RequestParam(name = "size") int size) {
        List<LocalDate> dates = productService.getEmployedDates(size);
        return dates;
    }

    @PostMapping("/orders")
    public void updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
    }

    @GetMapping("/orders/active/{user_id}")
    public Order getActiveOrder(@PathVariable Long user_id) {
        Order order = orderService.getActiveOrder(user_id);
        return order;
    }

    @PutMapping("/orders/active/{user_id}")
    public void updateActiveOrderStatus(@PathVariable Long user_id, @RequestBody OrderStatus status) {
        orderService.updateOrder(user_id, status);
    }

    @PutMapping("/orders/cancel/{user_id}")
    public void cancelActiveOrder(@PathVariable Long user_id) {
        orderService.cancelActiveOrder(user_id);
    }

    @PostMapping("/users/login")
    public JwtResponse authorizeUser(@RequestBody UserAuthDTO userAuthDTO) throws AuthenticationException {
        return userService.authorizeUser(userAuthDTO);
    }

    @PostMapping("/users/registration")
    public void registrationUser(@RequestBody UserRegDTO userRegDTO) throws AuthenticationException {
         userService.registrateUser(userRegDTO);

    }

    @PostMapping("/order")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order.getOrderTime(), order.getProducts(), order.getUser());
    }

    @PostMapping("/users/{userId}/order")
    public Order addOrderToUser(@PathVariable Long userId, @RequestBody Order order) {
        return orderService.addOrderToUser(order, userId);
    }


}

