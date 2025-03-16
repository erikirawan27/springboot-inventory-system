package com.example.Assignment.service;

import com.example.Assignment.dto.ItemResponse;
import com.example.Assignment.entity.Item;
import com.example.Assignment.exception.ResourceNotFoundException;
import com.example.Assignment.helper.StockHelper;
import com.example.Assignment.repository.ItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    private final StockHelper stockHelper;

    public ItemService(ItemRepository itemRepository, StockHelper stockHelper) {
        this.itemRepository = itemRepository;
        this.stockHelper = stockHelper;
    }


    public Page<ItemResponse> getAllItems(Pageable pageable) {
        Page<Item> items = itemRepository.findAll(pageable);

        List<ItemResponse> responseList = items.getContent().stream()
                .map(item -> new ItemResponse(item.getId(), item.getName(), item.getPrice(),
                        stockHelper.getAvailableStock(item.getId())))
                .toList();


        return new PageImpl<>(responseList, pageable, items.getTotalElements());
    }

    public  Item addItem(@Valid Item item) {
        return itemRepository.save(item);
    }

    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " not found"));
        return new ItemResponse(item.getId(), item.getName(), item.getPrice(), stockHelper.getAvailableStock(item.getId()));
    }

    @Transactional
    public Item editItem(Long id, Item updatedItem) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " not found"));

        item.setName(updatedItem.getName());
        item.setPrice(updatedItem.getPrice());

        return itemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Item with ID " + id + " not found for deletion");
        }
        itemRepository.deleteById(id);
    }
}
