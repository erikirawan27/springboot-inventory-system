package com.example.Assignment.helper;

import com.example.Assignment.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockHelper {

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Calculate available stock for a given item.
     */
    public int getAvailableStock(Long itemId) {
        Integer topupQty = inventoryRepository.sumQuantityByType(itemId, "T");
        Integer withdrawQty = inventoryRepository.sumQuantityByType(itemId, "W");

        return (topupQty == null ? 0 : topupQty) - (withdrawQty == null ? 0 : withdrawQty);
    }
}
