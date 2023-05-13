package com.example.demo.controller;

import com.example.demo.EntityDTO.UserAuthDTO;
import com.example.demo.EntityDTO.UserRegDTO;
import com.example.demo.EntityDTO.JwtResponse;
import com.example.demo.autorization.JwtTokenProvider;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@Tag(name = "UserController", description = "Управляет пользователями")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public UserController(UserService userService, OrderService orderService, ProductService productService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }


    @PostMapping("/user")
    @Operation(summary = "Создать пользователя", description = "Принимает UserRegDTO и создает пользователя")
    public User createUser(@RequestBody UserRegDTO user) {
        return userRepository.save(user.toUser());
    }


    @GetMapping("/users")
    public User getUserById(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long id = Long.parseLong(strId);
        return userRepository.getReferenceById(id);
    }

    @PostMapping("/users/add/{product_id}")
    public void addProductToCart(@RequestHeader("Authorization") String token, @PathVariable Long product_id, @RequestParam(name = "size") int size) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        userService.addProductToCart(user_id, product_id, size);
    }

    @GetMapping("/users/active")
    public Order getActiveOrder(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        return orderService.getActiveOrder(user_id);
    }

    @PutMapping("/users/active")
    public void updateActiveOrderStatus(@RequestHeader("Authorization") String token, @RequestBody OrderStatus status) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.updateOrder(user_id, status);
    }

    @PutMapping("/users/cancel")
    public void cancelActiveOrder(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.cancelActiveOrder(user_id);
    }

    @PostMapping("/users/login")
    public JwtResponse authorizeUser(@RequestBody UserAuthDTO userAuthDTO) throws AuthenticationException {
        return userService.authorizeUser(userAuthDTO);
    }

    @PostMapping("/users/registration")
    public JwtResponse registrationUser(@RequestBody UserRegDTO userRegDTO) throws AuthenticationException {
        userService.registrateUser(userRegDTO);
        return userService.authorizeUser(UserAuthDTO
                .builder()
                .username(userRegDTO.getPhoneNumber())
                .password(userRegDTO.getPassword())
                .build());
    }


    @PostMapping("/users/order")
    public Order addOrderToUser(@RequestHeader("Authorization") String token, @RequestBody Order order) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        return orderService.addOrderToUser(order, user_id);
    }


}

