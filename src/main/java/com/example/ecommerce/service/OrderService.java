package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public OrderResponse createOrder(long id, CreateOrderRequest request) {

        return new OrderResponse();

    }


}
