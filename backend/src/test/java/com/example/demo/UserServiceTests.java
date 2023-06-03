package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTests {

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;

    @Test
    void testGetByLogin_ExistingUser() {
        User expectedUser = new User(5L, "89529550132", "password", "Mary", null, Role.USER);
        when(userRepository.findByPhoneNumber("89529550132")).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getByLogin("89529550132");
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }

    @Test
    void testAddProductToCart_UserFound_AddsProductToCartingOrder() throws Exception {
        User user = new User(5L, "89529550132", "password", "Mary", new ArrayList<>(), Role.USER);
        Long id = 1L;
        double totalCost = 400;
        ZonedDateTime orderTime = ZonedDateTime.parse("2023-05-04T00:00:00+03:00[Europe/Moscow]");
        ZonedDateTime startTime = ZonedDateTime.parse("2023-05-04T00:00:00+03:00[Europe/Moscow]");
        ZonedDateTime finishTime = ZonedDateTime.parse("2023-05-04T00:00:00+03:00[Europe/Moscow]");
        List<Product> products = new ArrayList<>();
        OrderStatus orderStatus = OrderStatus.WAITING_FOR_RECEIVING;
        Order cartingOrder = new Order(id, totalCost, orderTime, startTime, finishTime, products, orderStatus, user);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(productRepository.getFirstBySizeAndProductPropertyId(anyDouble(), anyLong())).thenReturn(new Product());

        userService.addProductToCart(5L, 123L, 10.0, ZonedDateTime.parse("2023-05-04T00:00:00+03:00[Europe/Moscow]"));

        verify(productRepository, times(1)).getFirstBySizeAndProductPropertyId(10.0, 123L);
        verify(userRepository, times(1)).save(user);

        assertEquals(1, user.getOrders().size());
        assertEquals(cartingOrder, user.getOrders().get(0));
    }

    @Test
    void testGetCartingOrder_CartingOrderExists_ReturnsExistingCartingOrder() {
//        UserRepository userRepository = Mockito.mock(UserRepository.class);
//        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
//
//        User user = new User(5L, "89529550132", "password", "Mary", null, Role.USER);
//        Order cartingOrder = new Order();
//        cartingOrder.setOrderStatus(OrderStatus.CARTING);
//        user.getOrders().add(new Order(1L, 400.0, ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), new ArrayList<>(), OrderStatus.ACTIVE, user));
//        user.getOrders().add(cartingOrder);
//
//        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
//        when(productRepository.getProductBySizeAndProductPropertyId(anyDouble(), anyLong())).thenReturn(new Product());
//
//        UserService userService = new UserService(userRepository, orderRepository, productRepository, null, null);
//
//        userService.addProductToCart(5L, 123L, 10.0);
//
//        assertEquals(1, cartingOrder.getProducts().size());
//        verify(productRepository, times(1)).getProductBySizeAndProductPropertyId(10.0, 123L);
    }


}
