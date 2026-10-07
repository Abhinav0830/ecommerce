package com.example.ecommerce.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class  CreateOrderRequest {

    private List<OrderItemRequest> items;
}