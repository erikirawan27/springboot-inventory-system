package com.example.Assignment.service;

import com.example.Assignment.dto.InventoryRequest;
import com.example.Assignment.entity.Inventory;
import com.example.Assignment.entity.Item;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.repository.InventoryRepository;
import com.example.Assignment.repository.ItemRepository;
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
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory sampleInventory;
    private Item sampleItem;
    private InventoryRequest inventoryRequest;

    @BeforeEach
    void setUp() {
        sampleItem = new Item(1L, "Laptop", 1500.0);
        sampleInventory = new Inventory(1L, 5, "W", sampleItem);

        inventoryRequest = new InventoryRequest();
        inventoryRequest.setItem_id(1L);
        inventoryRequest.setQty(5);
        inventoryRequest.setType("W");
    }

    @Test
    void testGetAllInventory() {
        List<Inventory> inventories = List.of(sampleInventory);
        Page<Inventory> mockPage = new PageImpl<>(inventories, PageRequest.of(0, 5), 1);

        when(inventoryRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Inventory> result = inventoryService.getAllInventory(PageRequest.of(0, 5));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleInventory.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetInventoryById_Found() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleInventory));

        Inventory result = inventoryService.getInventoryById(1L);

        assertNotNull(result);
        assertEquals(sampleInventory.getId(), result.getId());
    }

    @Test
    void testGetInventoryById_NotFound() {
        when(inventoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryById(2L));
    }

    @Test
    void testCreateInventory_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);

        Inventory result = inventoryService.addInventory(inventoryRequest);

        assertNotNull(result);
        assertEquals(sampleInventory.getQty(), result.getQty());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testCreateInventory_ItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.addInventory(inventoryRequest));
    }

    @Test
    void testEditInventory_Success() {
        InventoryRequest updatedInventory = new InventoryRequest();
        updatedInventory.setItem_id(1L);
        updatedInventory.setQty(10);
        updatedInventory.setType("T");

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleInventory));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);

        Inventory result = inventoryService.editInventory(1L, updatedInventory);

        assertNotNull(result);
        assertEquals(10, result.getQty());
    }

    @Test
    void testEditInventory_NotFound() {
        when(inventoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.editInventory(2L, inventoryRequest));
    }

    @Test
    void testDeleteInventory_Success() {
        when(inventoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventoryRepository).deleteById(1L);

        inventoryService.delete(1L);

        verify(inventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteInventory_NotFound() {
        when(inventoryRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.delete(2L));
    }


}
