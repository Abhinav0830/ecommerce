package com.example.ecommerce.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {

    private String name;
    private double price;
    private Integer availableQuantity;
    private String category;
}
