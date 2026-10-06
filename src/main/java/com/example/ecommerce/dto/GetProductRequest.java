package com.example.ecommerce.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductRequest {

    private String category;
    private String search;

    @Min(value = 0, message = "Page cannot be negative")
    private int page =0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private int size=10;

    private String sortBy = "id";
    private String sortDir = "desc";

   // private String
}
