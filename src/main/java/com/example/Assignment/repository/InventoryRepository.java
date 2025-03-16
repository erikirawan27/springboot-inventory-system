package com.example.Assignment.repository;

import com.example.Assignment.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT SUM(i.qty) FROM Inventory i WHERE i.item.id = :itemId AND i.type = :type")
    Integer sumQuantityByType(@Param("itemId") Long itemId, @Param("type") String type);

}
