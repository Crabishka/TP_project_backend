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

    public void approveOrder(Long userId) {
        Order waiting = orderRepository.findOrderByUserIdAndOrderStatus(userId, OrderStatus.WAITING_FOR_RECEIVING);
        if (waiting != null) {
            waiting.setOrderStatus(OrderStatus.ACTIVE);
            orderRepository.save(waiting);
        }

    }

    public void finishOrder(Long userId) {
        Order activeOrder = orderRepository.findOrderByUserIdAndOrderStatus(userId, OrderStatus.ACTIVE);

        if (activeOrder != null) {
            activeOrder.setOrderStatus(OrderStatus.FINISHED);
            orderRepository.save(activeOrder);
        }
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

    public void orderOrder(long userId, ZonedDateTime date) {
        Order order = getActiveOrder(userId);
        // FIXME
        // проверка на то, что пользователь не бронирует товар, который уже занят
        // в этой проверке учитываем время
        order.setOrderStatus(OrderStatus.WAITING_FOR_RECEIVING);
        order.setOrderTime(date);
        orderRepository.save(order);
    }


    public void deleteProductFromUserOrder(long userId, Long productId, double size) {
        Order order = getActiveOrder(userId);
        for (Product product : order.getProducts()) {
            if (product.getProductProperty().getId().equals(productId) && product.getSize() == size) {
                order.getProducts().remove(product);
                order.setTotalCost(order.getTotalCost() - product.getProductProperty().getCost());
                if (order.getProducts().size() == 0) {
                    orderRepository.delete(order);
                } else {
                    orderRepository.save(order);
                }
                return;
            }
        }

    }

    public Product changeProductSize(long userId,
                                     Long productId,
                                     double size,
                                     double newSize) {
        Order order = getActiveOrder(userId);
        for (int i = 0; i < order.getProducts().size(); i++) {
            Product product = order.getProducts().get(i);
            if (product.getProductProperty().getId().equals(productId) && product.getSize() == size) {
                Product newProduct = productRepository.getFirstBySizeAndProductPropertyId(newSize, productId);
                if (newProduct != null) {
                    order.getProducts().set(i, newProduct);
                    orderRepository.save(order);
                    return newProduct;
                }

            }
        }
        return null;
    }
}
