package com.example.ecommerce.controller;


import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable long id, @RequestBody CreateOrderRequest request){

        OrderResponse response = orderService.createOrder(id,request);

        return ResponseEntity.ok(response);
    }

}
