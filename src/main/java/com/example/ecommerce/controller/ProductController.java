package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.dto.UpdateProductRequest;
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

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(@PathVariable String tenantName,
                                                             GetProductRequest request){

        Page<ProductResponse> products = productService.getProducts(tenantName,request);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id, @PathVariable String tenantName){
        ProductResponse response = productService.getProductById(id,tenantName);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable long id, @PathVariable String tenantName, @RequestBody UpdateProductRequest request){
        ProductResponse response = productService.updateProduct(id,tenantName,request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id, @PathVariable String tenantName) {

        productService.deleteProduct(id, tenantName);

        return ResponseEntity.ok(
                "Product with id: " + id + " and tenant: " + tenantName + " deleted successfully"
        );
    }

}
