package com.khoavdse170395.paymentservice.service;

import com.khoavdse170395.paymentservice.domain.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<OrderEntity> findById(Integer id);
    List<OrderEntity> findAll();
    List<OrderEntity> findByUserId(Long userId);
    OrderEntity create(OrderEntity order);
    OrderEntity updateStatus(Integer id, String status);
    OrderEntity cancelOrder(Integer id);
}
