package com.example.ecommerce.controller;

import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.service.UserProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class UserProductController {

    private final UserProductService userProductService;

    public UserProductController(
            UserProductService userProductService) {
        this.userProductService = userProductService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            GetProductRequest request) {

        return ResponseEntity.ok(
                userProductService.getProducts(request)
        );
    }
}