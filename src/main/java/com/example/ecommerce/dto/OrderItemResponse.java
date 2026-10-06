package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;

    private String productName;

    private BigDecimal price;
    private int quantity;
    private BigDecimal totalPrice;

}
