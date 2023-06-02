package com.example.demo.service;

import com.example.demo.EntityDTO.OrderDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class OrderService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ProductPropertiesRepository productPropertiesRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public OrderService(ModelMapper modelMapper,
                        ProductRepository productRepository,
                        ProductPropertiesRepository productPropertiesRepository,
                        OrderRepository orderRepository, UserRepository userRepository,
                        ProductService productService) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.productPropertiesRepository = productPropertiesRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;

        this.productService = productService;
    }

    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.get();
    }

    @Transactional
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

    @Transactional
    public Order getActiveOrder(Long userId) {
        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.CARTING);
        orderStatusList.add(OrderStatus.FITTING);
        orderStatusList.add(OrderStatus.WAITING_FOR_RECEIVING);
        orderStatusList.add(OrderStatus.ACTIVE);
        orderStatusList.add(OrderStatus.WAITING_FOR_PAYMENT);
        Order activeOrder = orderRepository.findOrderByUserIdAndOrderStatusIn(userId, orderStatusList);
        return activeOrder;
    }

    @Transactional
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

    @Transactional
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

    public void clearCart(Long userId) {
        Order order = orderRepository.findOrderByUserIdAndOrderStatus(userId, OrderStatus.CARTING);
        orderRepository.delete(order);
    }


    public void approveOrder(Long orderId) {
        Order waiting = orderRepository.findById(orderId).get();
        waiting.setOrderStatus(OrderStatus.FITTING);
        waiting.setStartTime(ZonedDateTime.now());
        orderRepository.save(waiting);

    }

    public void finishOrder(Long orderId) {
        Order activeOrder = orderRepository.findById(orderId).get();
        activeOrder.setFinishTime(ZonedDateTime.now());
        activeOrder.setOrderStatus(OrderStatus.WAITING_FOR_PAYMENT);
        orderRepository.save(activeOrder);
    }

    public void payOrder(Long orderId) {
        Order activeOrder = orderRepository.findById(orderId).get();
        activeOrder.setOrderStatus(OrderStatus.FINISHED);
        orderRepository.save(activeOrder);
    }

    public List<Order> getAllOrders(Long user_id) {
        List<Order> orderList = orderRepository.findAllByUserIdOrderById(user_id);
        return orderList;
    }

    @Transactional
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

    @Transactional
    public Order addOrderToUser(Order order, Long userId) {
        User user = userRepository.findById(userId).get();
        user.getOrders().add(order);
        order.setUser(user);
        userRepository.save(user);
        return order;
    }

    @Transactional
    public void makeOrder(long userId, ZonedDateTime date) {
        Order order = getActiveOrder(userId);
        order.setOrderStatus(OrderStatus.WAITING_FOR_RECEIVING);
        order.setOrderTime(date);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteProductFromUserOrder(long orderId, Long productId, double size) {
        Order order = orderRepository.findById(orderId).get();
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

    @Transactional
    public Product changeProductSize(long userId,
                                     Long productId,
                                     double size,
                                     double newSize) {
        Order order = getActiveOrder(userId);
        return changeProduct(productId, size, newSize, order);
    }


    @Transactional
    public void changeProductSizeByOrder(long orderId,
                                         Long productId,
                                         double size,
                                         double newSize) {
        Order order = orderRepository.findById(orderId).get();
        changeProduct(productId, size, newSize, order);
    }

    private Product changeProduct(Long productId, double size, double newSize, Order order) {
        for (int i = 0; i < order.getProducts().size(); i++) {
            Product product = order.getProducts().get(i);
            if (product.getProductProperty().getId().equals(productId) && product.getSize() == size) {
                List<Product> productList =
                        productService.getFreeProductByDateAndProductPropertyAndSize(
                                order.getOrderTime(),
                                productId,
                                newSize);
                Product newProduct = productList.get(0);
                if (newProduct != null) {
                    order.getProducts().set(i, newProduct);
                    orderRepository.save(order);
                    return newProduct;
                }

            }
        }
        return null;
    }


    public List<OrderDTO> getLastOrders() {
        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.ACTIVE);
        orderStatusList.add(OrderStatus.FITTING);
        orderStatusList.add(OrderStatus.WAITING_FOR_RECEIVING);
        orderStatusList.add(OrderStatus.WAITING_FOR_PAYMENT);
        List<Order> order = orderRepository.findTop30ByOrderStatusInOrderById(orderStatusList);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (int i = 0; i < order.size(); i++) {
            OrderDTO build = OrderDTO.builder().activeOrder(order.get(i)).name(order.get(i).getUser().getName()).phoneNumber(order.get(i).getUser().getPhoneNumber()).build();
            orderDTOList.add(build);
        }
        return orderDTOList;
    }

    public OrderDTO getActiveOrderByPhone(String phoneNumber) {


        User user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);
        if (user != null) {
            Order order = getActiveOrder(user.getId());
            return OrderDTO.builder().activeOrder(order).name(order.getUser().getName()).phoneNumber(order.getUser().getPhoneNumber()).build();
        } else {
            return null;
        }

    }

    public void cancelOrder(Long orderId) {
        Order activeOrder = orderRepository.findById(orderId).get();
        activeOrder.setFinishTime(ZonedDateTime.now());
        activeOrder.setOrderStatus(OrderStatus.CANCELED_BY_EMPLOYEE);
        orderRepository.save(activeOrder);
    }

}
