package com.example.Assignment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    @Pattern(regexp = "W|T", message = "Type must be 'W' (Withdraw) or 'T' (Topup)")
    private String type;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
