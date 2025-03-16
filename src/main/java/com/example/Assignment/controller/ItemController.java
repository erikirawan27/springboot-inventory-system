package com.example.Assignment.controller;

import com.example.Assignment.dto.ItemResponse;
import com.example.Assignment.entity.Item;
import com.example.Assignment.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Page<ItemResponse> getAllitem(Pageable pageable) {
        return itemService.getAllItems(pageable);
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.addItem(item);
    }

    @PutMapping("/{id}")
    public Item editItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        return itemService.editItem(id, updatedItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable Long id) {
        itemService.delete(id);
    }

}
