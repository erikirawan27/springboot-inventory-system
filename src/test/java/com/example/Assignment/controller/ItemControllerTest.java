package com.example.Assignment.controller;

import com.example.Assignment.dto.ItemResponse;
import com.example.Assignment.entity.Item;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.handler.GlobalExceptionHandler;
import com.example.Assignment.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private Item itemSmpl;
    private ItemResponse itemSmplResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        itemSmpl = new Item(1L, "laptop", 1500.0);
        itemSmplResponse = new ItemResponse(1L, "laptop", 1500.0, 5);
    }

    @Test
    void testGetItemById_Success() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(itemSmplResponse);

        mockMvc.perform(get("/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("laptop")))
                .andExpect(jsonPath("$.price", is(1500.0)))
                .andExpect(jsonPath("$.qty", is(5)));
    }

    @Test
    void testGetItemById_NotFound() throws Exception {
        when(itemService.getItemById(2L)).thenThrow(new ResourceNotFoundException("Item with ID 2 not found"));

        mockMvc.perform(get("/item/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Item with ID 2 not found")));

        verify(itemService, times(1)).getItemById(2L);
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.addItem(any(Item.class))).thenReturn(itemSmpl);

        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"laptop\", \"price\": 1500.0 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("laptop")))
                .andExpect(jsonPath("$.price", is(1500.0)));

        verify(itemService, times(1)).addItem(any(Item.class));
    }

    @Test
    void testEditItem_Success() throws Exception {
        Item updatedItem = new Item(1L, "Updated Laptop", 1300.0);

        when(itemService.editItem(eq(1L), any(Item.class))).thenReturn(updatedItem);

        mockMvc.perform(put("/item/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Updated Laptop\", \"price\": 1300.0 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(1300.0)));

        verify(itemService, times(1)).editItem(eq(1L), any(Item.class));
    }

    @Test
    void testEditItem_NotFound() throws Exception {
        when(itemService.editItem(eq(2L), any(Item.class))).thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(put("/item/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Updated Laptop\", \"price\": 1300.0 }"))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).editItem(eq(2L), any(Item.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        doNothing().when(itemService).delete(1L);

        mockMvc.perform(delete("/item/1"))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(1L);
    }

}
