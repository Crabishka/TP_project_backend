package com.example.demo.EntityDTO;

import com.example.demo.entity.Order;
import lombok.Builder;

@Builder
public class OrderDTO {

    private String phoneNumber;
    private String name;
    private Order activeOrder;

    public OrderDTO(String phoneNumber, String name, Order activeOrder) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.activeOrder = activeOrder;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Order getActiveOrder() {
        return activeOrder;
    }

    public void setActiveOrder(Order activeOrder) {
        this.activeOrder = activeOrder;
    }
}
