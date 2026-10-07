package com.example.ecommerce.dto;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private long id;
    private String name;
    private double price;
    private int availableQuantity;
    private String category;
}
