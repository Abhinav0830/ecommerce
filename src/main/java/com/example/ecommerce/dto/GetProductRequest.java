package com.example.ecommerce.dto;


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
    private int page =0;
    private int size=10;
    private String sortBy = "id";
    private String sortDir = "desc";

   // private String
}
