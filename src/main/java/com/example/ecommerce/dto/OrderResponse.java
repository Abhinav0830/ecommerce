package com.example.ecommerce.dto;

import com.example.ecommerce.entity.OrderItem;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private long id;
    private int totalQuantity;
    private double totalPrice;
    private List<OrderItemResponse> orderItems;
}
