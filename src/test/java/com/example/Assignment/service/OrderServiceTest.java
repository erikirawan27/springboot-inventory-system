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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockHelper stockHelper;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;
    private Item sampleItem;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        sampleItem = new Item(1L, "Laptop", 1000.0);
        sampleOrder = new Order(1L, 2, 1000.0, sampleItem);

        orderRequest = new OrderRequest();
        orderRequest.setItem_id(1L);
        orderRequest.setQty(2);
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = List.of(sampleOrder);
        Page<Order> mockPage = new PageImpl<>(orders, PageRequest.of(0, 5), 1);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Order> result = orderService.getAllOrders(PageRequest.of(0, 5));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleOrder.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetOrderById_Found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        Order result = orderService.getItemById(1L);

        assertNotNull(result);
        assertEquals(sampleOrder.getId(), result.getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getItemById(2L));
    }

    @Test
    void testCreateOrder_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(stockHelper.getAvailableStock(1L)).thenReturn(10);
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Order result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(sampleOrder.getQty(), result.getQty());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(stockHelper.getAvailableStock(1L)).thenReturn(1);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    void testEditOrder_Success() {
        OrderRequest updatedOrder = new OrderRequest();
        updatedOrder.setItem_id(1L);
        updatedOrder.setQty(3);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(stockHelper.getAvailableStock(1L)).thenReturn(10);
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Order result = orderService.editOrder(1L, updatedOrder);

        assertNotNull(result);
        assertEquals(3, result.getQty());
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
    }

    @Test
    void testEditOrder_NotFound() {
        OrderRequest updatedOrder = new OrderRequest();
        updatedOrder.setItem_id(1L);
        updatedOrder.setQty(3);

        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.editOrder(2L, updatedOrder));
    }
}
