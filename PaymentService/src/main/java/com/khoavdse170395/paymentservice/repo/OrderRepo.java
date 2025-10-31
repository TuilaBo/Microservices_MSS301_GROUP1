package com.khoavdse170395.paymentservice.repo;


import com.khoavdse170395.paymentservice.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<OrderEntity, Integer> {}
