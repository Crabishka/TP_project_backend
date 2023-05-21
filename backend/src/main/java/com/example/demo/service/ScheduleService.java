package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.OrderRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component

public class ScheduleService {
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public ScheduleService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 1000)
    public void processFittingOrders() {
        List<Order> fittingOrders = orderRepository.findByOrderStatus(OrderStatus.FITTING);
        ZonedDateTime currentTime = ZonedDateTime.now();

        for (Order order : fittingOrders) {
            ZonedDateTime startTime = order.getStartTime();
            double timeBetween = ChronoUnit.MINUTES.between(startTime, currentTime);
            if (timeBetween > 9) {
                order.setOrderStatus(OrderStatus.ACTIVE);

                orderService.updateOrder(order);
            }
        }
    }
}
