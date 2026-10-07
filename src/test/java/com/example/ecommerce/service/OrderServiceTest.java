package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderItemRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderService orderService;


    @Test
    void createOrder_shouldCreateOrderSuccessfully() {

        User user = User.builder()
                .username("abhinav")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(10)
                .build();

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of(itemRequest))
                .build();

        Order savedOrder = Order.builder()
                .id(100L)
                .user(user)
                .totalQuantity(2)
                .totalPrice(100000)
                .build();

        when(authentication.getName()).thenReturn("abhinav");
        when(userRepository.findByUsername("abhinav"))
                .thenReturn(Optional.of(user));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        OrderResponse response =
                orderService.createOrder(authentication, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(2, response.getTotalQuantity());
        assertEquals(100000, response.getTotalPrice());

        assertEquals(8, product.getAvailableQuantity());

        verify(userRepository).findByUsername("abhinav");
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
    }


    @Test
    void createOrder_shouldThrowException_whenUserDoesNotExist() {

        when(authentication.getName()).thenReturn("abhinav");

        when(userRepository.findByUsername("abhinav"))
                .thenReturn(Optional.empty());

        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of())
                .build();

        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(authentication, request)
        );

        verify(userRepository).findByUsername("abhinav");

        verifyNoInteractions(
                productRepository,
                orderRepository,
                orderItemRepository
        );
    }


    @Test
    void createOrder_shouldThrowException_whenProductDoesNotExist() {

        User user = User.builder()
                .username("abhinav")
                .build();

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of(itemRequest))
                .build();

        when(authentication.getName()).thenReturn("abhinav");
        when(userRepository.findByUsername("abhinav"))
                .thenReturn(Optional.of(user));
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder(authentication, request)
        );

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }


    @Test
    void createOrder_shouldThrowException_whenInsufficientStock() {

        User user = User.builder()
                .username("abhinav")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(2)
                .build();

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of(itemRequest))
                .build();

        when(authentication.getName()).thenReturn("abhinav");
        when(userRepository.findByUsername("abhinav"))
                .thenReturn(Optional.of(user));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(authentication, request)
        );

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }
}