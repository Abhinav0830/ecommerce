package com.example.ecommerce.controller;


import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.service.OrderService;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(Authentication auth,
                                                     @Valid @RequestBody CreateOrderRequest request){

        OrderResponse response = orderService.createOrder(auth,request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(Authentication auth){

        return ResponseEntity.ok(orderService.getOrderHistory(auth));
    }

}
