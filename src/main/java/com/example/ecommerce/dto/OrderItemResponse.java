package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private long id;

    private String productName;

    private double price;
    private int quantity;
    private double totalPrice;

}
