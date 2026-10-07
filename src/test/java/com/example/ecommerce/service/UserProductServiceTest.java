package com.example.ecommerce.service;

import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UserProductService userProductService;


    @Test
    void getProducts_shouldReturnProductsSuccessfully() {

        GetProductRequest request = GetProductRequest.builder()
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDir("asc")
                .category("Electronics")
                .search("laptop")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(5)
                .category("Electronics")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findAllWithFiltersAndDeletedFalse(
                eq("Electronics"),
                eq("laptop"),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                userProductService.getProducts(request);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        ProductResponse response = result.getContent().get(0);

        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(50000, response.getPrice());
        assertEquals(5, response.getAvailableQuantity());
        assertEquals("Electronics", response.getCategory());

        verify(productRepository)
                .findAllWithFiltersAndDeletedFalse(
                        eq("Electronics"),
                        eq("laptop"),
                        any(Pageable.class)
                );
    }


    @Test
    void getProducts_shouldUseDefaultValues_whenPaginationAndSortingAreInvalid() {

        GetProductRequest request = GetProductRequest.builder()
                .page(-1)
                .size(0)
                .sortBy(" ")
                .sortDir(" ")
                .category(" ")
                .search(" ")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findAllWithFiltersAndDeletedFalse(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                userProductService.getProducts(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        verify(productRepository)
                .findAllWithFiltersAndDeletedFalse(
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                );
    }


    @Test
    void getProducts_shouldUseDescendingSort_whenSortDirectionIsDesc() {

        GetProductRequest request = GetProductRequest.builder()
                .page(1)
                .size(5)
                .sortBy("price")
                .sortDir("desc")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findAllWithFiltersAndDeletedFalse(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                userProductService.getProducts(request);

        assertNotNull(result);

        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());

        verify(productRepository)
                .findAllWithFiltersAndDeletedFalse(
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                );
    }


    @Test
    void getProducts_shouldTrimCategoryAndSearch() {

        GetProductRequest request = GetProductRequest.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .sortDir("asc")
                .category(" Electronics ")
                .search(" laptop ")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findAllWithFiltersAndDeletedFalse(
                eq("Electronics"),
                eq("laptop"),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                userProductService.getProducts(request);

        assertNotNull(result);

        verify(productRepository)
                .findAllWithFiltersAndDeletedFalse(
                        eq("Electronics"),
                        eq("laptop"),
                        any(Pageable.class)
                );
    }


    @Test
    void getProducts_shouldReturnEmptyPage_whenNoProductsExist() {

        GetProductRequest request = GetProductRequest.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .sortDir("asc")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findAllWithFiltersAndDeletedFalse(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                userProductService.getProducts(request);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(productRepository)
                .findAllWithFiltersAndDeletedFalse(
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                );
    }
}