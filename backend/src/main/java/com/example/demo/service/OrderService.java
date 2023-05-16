package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ProductPropertiesRepository productPropertiesRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(ModelMapper modelMapper, ProductRepository productRepository, ProductPropertiesRepository productPropertiesRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.productPropertiesRepository = productPropertiesRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.get();
    }

    public void updateOrder(Order order) {
        if (order.getId() != null) {
            Order existingOrder = getOrderById((long) order.getId());
            if (existingOrder != null) {
                existingOrder.setProducts(order.getProducts());
                existingOrder.setOrderTime(order.getOrderTime());
                existingOrder.setOrderStatus(order.getOrderStatus());
                existingOrder.setTotalCost(order.getTotalCost());
                existingOrder.setStartTime(order.getStartTime());
                existingOrder.setFinishTime(order.getFinishTime());
                orderRepository.save(existingOrder);
            }
        }
    }

    public Order getActiveOrder(Long userId) {
        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.CARTING);
        orderStatusList.add(OrderStatus.FITTING);
        orderStatusList.add(OrderStatus.WAITING_FOR_RECEIVING);
        Order activeOrder = orderRepository.findOrderByUserIdAndOrderStatusIn(userId, orderStatusList);
        return activeOrder;
    }

    public void updateOrder(Long userId, OrderStatus status) {

        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.CARTING);
        orderStatusList.add(OrderStatus.FITTING);
        orderStatusList.add(OrderStatus.WAITING_FOR_RECEIVING);
        Order activeOrder = orderRepository.findOrderByUserIdAndOrderStatusIn(userId, orderStatusList);

        if (activeOrder != null) {
            activeOrder.setOrderStatus(status);
            orderRepository.save(activeOrder);
        }
    }


    public void cancelActiveOrder(Long userId) {
        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.CARTING);
        orderStatusList.add(OrderStatus.FITTING);
        orderStatusList.add(OrderStatus.WAITING_FOR_RECEIVING);
        Order activeOrder = orderRepository.findOrderByUserIdAndOrderStatusIn(userId, orderStatusList);

        if (activeOrder != null) {
            activeOrder.setOrderStatus(OrderStatus.CANCELED_BY_USER);
            orderRepository.save(activeOrder);
        }

    }

    public void approveOrder(Long orderId) {
        Order waiting = orderRepository.findById(orderId).get();
        waiting.setOrderStatus(OrderStatus.FITTING);
        orderRepository.save(waiting);

    }

    public void finishOrder(Long orderId) {
        Order activeOrder = orderRepository.findById(orderId).get();

        activeOrder.setOrderStatus(OrderStatus.FINISHED);
        orderRepository.save(activeOrder);
    }

    public List<Order> getAllOrders(Long user_id) {
        List<Order> orderList = orderRepository.findAllByUserId(user_id);
        return orderList;
    }

    public Order createOrder(ZonedDateTime orderTime, List<Product> products, User user) {
        Order order = new Order();
        order.setOrderTime(orderTime);
        order.setProducts(products);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setTotalCost(calculateTotalCost(products));
        order = orderRepository.save(order);
        return order;
    }

    private double calculateTotalCost(List<Product> products) {
        double totalCost = 0;
        for (Product product : products) {
            totalCost += product.getProductProperty().getCost();
        }
        return totalCost;
    }

    public Order addOrderToUser(Order order, Long userId) {
        User user = userRepository.findById(userId).get();
        user.getOrders().add(order);
        order.setUser(user);
        userRepository.save(user);
        return order;
    }
}
