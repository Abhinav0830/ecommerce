package com.example.ecommerce.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Positive(message = "Price must be greater than 0")
    private double price;


    private int availableQuantity;

    @NotBlank(message = "Category is required")
    private String category;
}
