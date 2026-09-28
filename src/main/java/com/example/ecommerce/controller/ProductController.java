package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{tenantName}/products")
public class ProductController {

    ProductService productService;

    public ProductController (ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createNewProduct(@PathVariable String tenantName, @RequestBody CreateProductRequest request){
        ProductResponse response = productService.createProduct(tenantName,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @GetMapping
//    public ResponseEntity<Page<ProductResponse>> getProducts()


}
