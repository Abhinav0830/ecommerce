package com.example.ecommerce.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Size(min = 2, max = 30)
    private String name;

    @Positive(message = "Price must be greater than 0")
    private double price;

    @Positive(message = "Quantity cannot be negative")
    private int availableQuantity;

    @NotBlank(message = "Category is required")
    private String category;
}
