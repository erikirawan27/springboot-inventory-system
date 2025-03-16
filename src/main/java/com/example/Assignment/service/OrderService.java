package com.example.Assignment.service;

import com.example.Assignment.dto.OrderRequest;
import com.example.Assignment.entity.Inventory;
import com.example.Assignment.entity.Item;
import com.example.Assignment.entity.Order;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.helper.StockHelper;
import com.example.Assignment.repository.InventoryRepository;
import com.example.Assignment.repository.ItemRepository;
import com.example.Assignment.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockHelper stockHelper;

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order getItemById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (orderRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Order with ID " + id + " not found for deletion");
        }
        itemRepository.deleteById(id);
    }

    @Transactional
    public Order createOrder(@Valid OrderRequest orderReq) {
        Item item = itemRepository.findById(orderReq.getItem_id())
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + orderReq.getItem_id() + " not found"));

        int itemQty = stockHelper.getAvailableStock(item.getId());

        if (orderReq.getQty() > itemQty) {
            throw new IllegalArgumentException("Not enough stock. Available stock : " + itemQty);
        }

        Order order = new Order();

        return setOrder(item, order, orderReq);
    }

    @Transactional
    public Order editOrder(Long id, OrderRequest updatedOrder) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        Item item = itemRepository.findById(updatedOrder.getItem_id())
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + updatedOrder.getItem_id() + " not found"));


//        return stock to inventory
        Inventory returnStock = new Inventory();
        returnStock.setItem(order.getItem());
        returnStock.setQty(order.getQty());
        returnStock.setType("T");
        inventoryRepository.save(returnStock);

        int itemQty = stockHelper.getAvailableStock(item.getId());

        if (updatedOrder.getQty() > itemQty) {
            throw new IllegalArgumentException("Not enough stock. Available stock : " + itemQty);
        }

        return setOrder(item, order, updatedOrder);
    }

    private Order setOrder(Item item, Order order, OrderRequest orderRequest) {
        order.setQty(orderRequest.getQty());
        order.setPrice(item.getPrice());
        order.setItem(item);
        Order savedOrder = orderRepository.save(order);

        Inventory withdraw = new Inventory();
        withdraw.setItem(item);
        withdraw.setQty(orderRequest.getQty());
        withdraw.setType("W");
        inventoryRepository.save(withdraw);

        return savedOrder;
    }

}
