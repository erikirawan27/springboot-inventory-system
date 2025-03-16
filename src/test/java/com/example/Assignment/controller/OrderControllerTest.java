package com.example.Assignment.controller;


import com.example.Assignment.dto.ItemResponse;
import com.example.Assignment.dto.OrderRequest;
import com.example.Assignment.entity.Item;
import com.example.Assignment.entity.Order;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.handler.GlobalExceptionHandler;

import com.example.Assignment.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getItemById(2L)).thenThrow(new ResourceNotFoundException("Order with ID 2 not found"));

        mockMvc.perform(get("/order/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with ID 2 not found"));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItem_id(1L);
        orderRequest.setQty(2);

        Order mockOrder = new Order();
        mockOrder.setId(1L);

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(mockOrder);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"item_id\": 1, \"qty\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteOrder() throws Exception {
        doNothing().when(orderService).delete(1L);

        mockMvc.perform(delete("/order/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).delete(1L);
    }
}
