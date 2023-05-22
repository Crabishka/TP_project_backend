package com.example.demo.repository;

import com.example.demo.EntityDTO.OrderDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findOrderByOrderTime(ZonedDateTime date);

    Order findOrderByUserIdAndOrderStatus(Long user_id, OrderStatus orderStatus);

    List<Order> findAllByUserIdOrderById(Long user_id);

    Order findOrderByUserIdAndOrderStatusIn(Long user_id, List<OrderStatus> orderStatusList);

    List<Order> findTop30ByOrderStatusInOrderById(List<OrderStatus> orderStatus);

    Order findOrderByOrderStatusAndUser_PhoneNumber(OrderStatus orderStatus, String phoneNumber);

    List<Order> findByOrderStatus(OrderStatus orderStatus);
}
