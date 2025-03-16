package com.example.Assignment.service;

import com.example.Assignment.controller.ItemController;
import com.example.Assignment.dto.ItemResponse;
import com.example.Assignment.entity.Item;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.handler.GlobalExceptionHandler;
import com.example.Assignment.helper.StockHelper;
import com.example.Assignment.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
public class ItemServiceTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Mock
    private StockHelper stockHelper;

    private Item itemSmpl;
    private ItemResponse itemSmplResponse;

    @BeforeEach
    void setUp() {
        itemSmpl = new Item(1L, "Laptop", 1200.0);
    }

    @Test
    void testAddItem() {
        when(itemRepository.save(itemSmpl)).thenReturn(itemSmpl);

        Item result = itemService.addItem(itemSmpl);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(1200.0, result.getPrice());
    }

    @Test
    void testGetAllItems() {
        List<Item> itemList = Arrays.asList(itemSmpl);
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 1);

        when(itemRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
        when(stockHelper.getAvailableStock(1L)).thenReturn(5);

        Page<ItemResponse> response = itemService.getAllItems(PageRequest.of(0, 5));

        assertFalse(response.isEmpty());
        assertEquals(1, response.getContent().size());
        assertEquals("Laptop", response.getContent().get(0).getName());
        assertEquals(5, response.getContent().get(0).getQty());
    }

    @Test
    void testGetItemById_Found() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSmpl));
        when(stockHelper.getAvailableStock(1L)).thenReturn(10);

        ItemResponse response = itemService.getItemById(1L);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(10, response.getQty());
    }

    @Test
    void testGetItemById_NotFound() {
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(2L));
    }

    @Test
    void testDeleteItem_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSmpl));

        itemService.delete(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteItem_NotFound() {
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.delete(2L));
    }

    @Test
    void testEditItem_Success() {
        Item updatedItem = new Item(1L, "Updated Laptop", 1300.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSmpl));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        Item result = itemService.editItem(1L, updatedItem);

        assertNotNull(result);
        assertEquals("Updated Laptop", result.getName());
        assertEquals(1300.0, result.getPrice());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testEditItem_NotFound() {
        Item updatedItem = new Item(1L, "Updated Laptop", 1300.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.editItem(1L, updatedItem));
        verify(itemRepository, never()).save(any(Item.class));
    }

}
