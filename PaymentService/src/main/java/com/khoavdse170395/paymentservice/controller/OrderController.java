package com.khoavdse170395.paymentservice.controller;

import com.khoavdse170395.paymentservice.domain.OrderEntity;
import com.khoavdse170395.paymentservice.service.OrderServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEntity> getById(@PathVariable Integer id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderEntity>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<OrderEntity> create(@RequestBody OrderEntity order) {
        OrderEntity created = orderService.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderEntity> cancel(@PathVariable Integer id) {
        OrderEntity canceled = orderService.cancelOrder(id);
        return ResponseEntity.ok(canceled);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderEntity> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        OrderEntity updated = orderService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
