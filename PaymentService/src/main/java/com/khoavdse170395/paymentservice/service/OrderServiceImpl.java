package com.khoavdse170395.paymentservice.service;

import com.khoavdse170395.paymentservice.domain.OrderEntity;
import com.khoavdse170395.paymentservice.exception.ResourceNotFoundException;
import com.khoavdse170395.paymentservice.repo.OrderRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;

    public OrderServiceImpl(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public Optional<OrderEntity> findById(Integer id) {
        return orderRepo.findById(id);
    }

    @Override
    public List<OrderEntity> findAll() {
        return orderRepo.findAll();
    }

    @Override
    public List<OrderEntity> findByUserId(Long userId) {
        return orderRepo.findAll().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderEntity create(OrderEntity order) {
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("CREATED");
        }
        return orderRepo.save(order);
    }

    @Override
    @Transactional
    public OrderEntity updateStatus(Integer id, String status) {
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id=" + id));
        order.setStatus(status);
        return orderRepo.save(order);
    }

    @Override
    @Transactional
    public OrderEntity cancelOrder(Integer id) {
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id=" + id));
        order.setStatus("CANCELED");
        return orderRepo.save(order);
    }
}

