package com.example.Assignment.controller;

import com.example.Assignment.dto.OrderRequest;
import com.example.Assignment.entity.Order;
import com.example.Assignment.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Page<Order> getAllStock(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @GetMapping("/{id}")
    public Order getItemById(@PathVariable Long id) {
        return orderService.getItemById(id);
    }

    @PostMapping
    public Order createItem(@RequestBody OrderRequest order) {
        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public Order editOrder(@PathVariable Long id, @RequestBody OrderRequest updatedOrder) {
        return orderService.editOrder(id, updatedOrder);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        orderService.delete(id);
    }
}
