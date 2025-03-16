package com.example.Assignment.controller;

import com.example.Assignment.dto.InventoryRequest;
import com.example.Assignment.entity.Inventory;
import com.example.Assignment.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public Page<Inventory> getAllStock(Pageable pageable) {
        return inventoryService.getAllInventory(pageable);
    }

    @GetMapping("/{id}")
    public Inventory getItemById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    @PostMapping
    public Inventory createInventory(@Valid @RequestBody InventoryRequest inventory) {
        return inventoryService.addInventory(inventory);
    }

    @PutMapping("/{id}")
    public Inventory editInventory(@PathVariable Long id, @Valid @RequestBody InventoryRequest inventory ) {
        return inventoryService.editInventory(id, inventory);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}
