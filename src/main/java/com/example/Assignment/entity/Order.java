package com.example.Assignment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

}
