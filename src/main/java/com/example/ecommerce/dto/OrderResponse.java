package com.example.ecommerce.dto;

import com.example.ecommerce.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private long id;
    private int totalQuantity;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> orderItems;
}
