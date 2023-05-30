package com.example.demo.controller;

import com.example.demo.EntityDTO.UserAuthDTO;
import com.example.demo.EntityDTO.UserRefreshDTO;
import com.example.demo.EntityDTO.UserRegDTO;
import com.example.demo.EntityDTO.JwtResponse;
import com.example.demo.autorization.JwtTokenProvider;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.time.ZonedDateTime;

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


    @PostMapping("/users")
    @Operation(summary = "Создать пользователя", description = "Принимает UserRegDTO и создает пользователя")
    public User createUser(@RequestBody UserRegDTO user) {
        return userRepository.save(user.toUser());
    }


    @GetMapping("/users")
    @Operation(summary = "Найти пользователя по id", description = "Принимает id пользователя")
    public User getUserById(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long id = Long.parseLong(strId);
        return userRepository.getReferenceById(id);
    }

    @PostMapping("/users/add/{product_id}")
    @Operation(summary = "Добавить продукт в корзину", description = "Принимает autorization?, id продукта и размер")
    public void addProductToCart(
            @RequestHeader("Authorization") String token,
            @PathVariable Long product_id,
            @RequestParam(name = "size") double size,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "dd-MM-yyyy") ZonedDateTime date) throws Exception {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        userService.addProductToCart(user_id, product_id, size, date);
    }


    @GetMapping("/users/active")
    @Operation(summary = "Получить активный заказ", description = "Принимает UserRegDTO и создает пользователя")
    public Order getActiveOrder(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        return orderService.getActiveOrder(user_id);
    }

    @PutMapping("/users/active")
    @Operation(summary = "Изменить активный статус заказа", description = "Принимает token и нужный статус")
    public void updateActiveOrderStatus(@RequestHeader("Authorization") String token, @RequestBody OrderStatus status) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.updateOrder(user_id, status);
    }


    @PostMapping("/users/active")
    @Operation(summary = "Совершается заказ товаров",
            description = "Принимает token пользователя и дату, на которую заказ совершен")
    public void createActiveOrderStatus(@RequestHeader("Authorization") String token,
                                        @RequestParam(name = "date") @DateTimeFormat(pattern = "dd-MM-yyyy") ZonedDateTime date) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.makeOrder(user_id, date);
    }

    @PutMapping("/users/delete/{product_id}")
    @Operation(summary = "Удаляет товар из активного заказа",
            description = "Принимает token пользователя, продукт и его размер")
    public void deleteProductFromOrder(@RequestHeader("Authorization") String token,
                                       @PathVariable Long product_id,
                                       @RequestParam(name = "size") double size) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.deleteProductFromUserOrder(orderService.getActiveOrder(user_id).getId(), product_id, size);
    }

    @PutMapping("/users/cancel")
    @Operation(summary = "Удалить активный заказ", description = "Принимает token")
    public void cancelActiveOrder(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.cancelActiveOrder(user_id);
    }

    @PutMapping("/users/cancel_cart")
    @Operation(summary = "Очистка корзины", description = "Принимает token")
    public void cancelCartingOrder(@RequestHeader("Authorization") String token) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        orderService.clearCart(user_id);
    }

    @PostMapping("/users/login")
    @Operation(summary = "Авторизовать пользователя", description = "Принимает UserAuthDTO")
    public JwtResponse authorizeUser(@RequestBody UserAuthDTO userAuthDTO) throws AuthenticationException {
        return userService.authorizeUser(userAuthDTO);
    }

    @PostMapping("/users/registration")
    @Operation(summary = "Регистрация пользователя", description = "Принимает UserRegDTO")
    public JwtResponse registrationUser(@RequestBody UserRegDTO userRegDTO, HttpServletResponse res) throws AuthenticationException {
        try {
            userService.registrantUser(userRegDTO);
        }catch (Exception e){
            res.setStatus(409);
            return null;
        }


        return userService.authorizeUser(UserAuthDTO
                .builder()
                .username(userRegDTO.getPhoneNumber())
                .password(userRegDTO.getPassword())
                .build());
    }

    @PostMapping("/users/refresh")
    public ResponseEntity<UserRefreshDTO> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.refreshToken(refreshToken));
    }


    @PostMapping("/users/order")
    @Operation(summary = "Добавить заказ к пользователю", description = "Принимает token пользователя и Order")
    public Order addOrderToUser(@RequestHeader("Authorization") String token, @RequestBody Order order) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        return orderService.addOrderToUser(order, user_id);
    }

    @PutMapping("/users/change/{product_id}")
    public Product changeProductSize(@RequestHeader("Authorization") String token,
                                     @PathVariable Long product_id,
                                     @RequestParam(name = "size") double size,
                                     @RequestParam(name = "new_size") double newSize) {
        String strId = jwtTokenProvider.getCustomClaimValue(token, "id");
        long user_id = Long.parseLong(strId);
        return orderService.changeProductSize(user_id, product_id, size, newSize);
    }


}

