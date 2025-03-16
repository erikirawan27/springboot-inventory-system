package com.example.Assignment.controller;

import com.example.Assignment.dto.InventoryRequest;
import com.example.Assignment.entity.Inventory;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.handler.GlobalExceptionHandler;
import com.example.Assignment.service.InventoryService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetInventoryById_NotFound() throws Exception {
        when(inventoryService.getInventoryById(2L)).thenThrow(new ResourceNotFoundException("Inventory with ID 2 not found"));

        mockMvc.perform(get("/inventory/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Inventory with ID 2 not found"));

        verify(inventoryService, times(1)).getInventoryById(2L);
    }

    @Test
    void testCreateInventory_Success() throws Exception {
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setItem_id(1L);
        inventoryRequest.setQty(5);
        inventoryRequest.setType("W");

        Inventory mockInventory = new Inventory();
        mockInventory.setId(1L);

        when(inventoryService.addInventory(any(InventoryRequest.class))).thenReturn(mockInventory);

        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"item_id\": 1, \"qty\": 5, \"type\": \"W\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventoryService, times(1)).addInventory(any(InventoryRequest.class));
    }

    @Test
    void testDeleteInventory() throws Exception {
        doNothing().when(inventoryService).delete(1L);

        mockMvc.perform(delete("/inventory/1"))
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).delete(1L);
    }

}
