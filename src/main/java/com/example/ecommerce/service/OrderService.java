package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderItemRequest;
import com.example.ecommerce.dto.OrderItemResponse;
import com.example.ecommerce.dto.OrderResponse;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    public OrderService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository,OrderItemRepository orderItemRepository){
        this.userRepository = userRepository;
        this.productRepository=productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderResponse createOrder(Authentication auth, CreateOrderRequest request) {

        String username = auth.getName();

        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()){
            throw new RuntimeException("User not Found");
        }

        Order order = new Order();
        order.setUser(user.get());

        int totalQuantity =0;
        double totalPrice = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        for(OrderItemRequest itemRequest : request.getItems()){

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(()->new ResourceNotFoundException("Product not found"));

            int requestedQuantity = itemRequest.getQuantity();
            if(requestedQuantity> product.getAvailableQuantity()){
                throw  new InsufficientStockException("Requested Quantity not available");
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItemList.add(orderItem);

            product.setAvailableQuantity(product.getAvailableQuantity()-requestedQuantity);
            totalQuantity += requestedQuantity;
            totalPrice+=(product.getPrice())*requestedQuantity;

            productRepository.save(product);

        }
        order.setOrderItems(orderItemList);
        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItemList);
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for(OrderItem item : orderItemList){
            OrderItemResponse response = new OrderItemResponse(
                    item.getId(),
                    item.getProduct().getName(),
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getQuantity()*(item.getProduct().getPrice()));
            itemResponses.add(response);

        }
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalQuantity(),
                savedOrder.getTotalPrice(),
                itemResponses);
    }

    @Transactional
    public List<OrderResponse> getOrderHistory(Authentication auth) {

        Optional<User> user = userRepository.findByUsername(auth.getName());

        if(user.isEmpty()){
            throw new ResourceNotFoundException("No such user found!");
        }

        List<Order> orders = orderRepository.findByUser(user.get());
        List<OrderResponse> responses = new ArrayList<>();

        for(Order order : orders){
            List<OrderItemResponse> itemResponses = new ArrayList<>();
            for(OrderItem item : order.getOrderItems()){
                Product product = item.getProduct();

                OrderItemResponse orderItemResponse = new OrderItemResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        item.getQuantity(),
                        item.getQuantity()* product.getPrice()
                );
                itemResponses.add(orderItemResponse);
            }

            OrderResponse response = new OrderResponse(
                    order.getId(),
                    order.getTotalQuantity(),
                    order.getTotalPrice(),
                    itemResponses
            );
            responses.add(response);
        }
        return responses;

    }
}
