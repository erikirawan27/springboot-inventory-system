package com.example.Assignment.service;

import com.example.Assignment.dto.InventoryRequest;
import com.example.Assignment.entity.Inventory;
import com.example.Assignment.entity.Item;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.repository.InventoryRepository;
import com.example.Assignment.repository.ItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private final ItemRepository itemRepository;

    public InventoryService(InventoryRepository inventoryRepository, ItemRepository itemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
    }

    public Page<Inventory> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    @Transactional
    public  Inventory addInventory(@Valid InventoryRequest req) {
        Item item = itemRepository.findById(req.getItem_id())
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + req.getItem_id() + " not found"));

        Inventory inventory = new Inventory();
        inventory.setQty(req.getQty());
        inventory.setType(req.getType());
        inventory.setItem(item);
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID " + id + " not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory with ID " + id + " not found for deletion");
        }
        inventoryRepository.deleteById(id);
    }

    @Transactional
    public Inventory editInventory(Long id, InventoryRequest req) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID " + id + " not found"));

        Item item = itemRepository.findById(req.getItem_id())
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + req.getItem_id() + " not found"));

        inventory.setQty(req.getQty());
        inventory.setItem(item);
        return inventoryRepository.save(inventory);
    }
}
