package com.example.demo;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderServiceTests {
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;

    @Test
    void testGetOrderById() {
        Order order = new Order(1L, 400, ZonedDateTime.parse("2023-05-04T00:04Z"), ZonedDateTime.parse("2023-05-04T00:04Z"), ZonedDateTime.parse("2023-05-04T00:04Z"), null, OrderStatus.WAITING_FOR_RECEIVING, null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Order returnedOrder = orderService.getOrderById(1L);
        assertEquals(order, returnedOrder);
    }
}
