package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order saveOrder(Order order) {
        System.out.println("Saving order: " + order);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email);
    }
}