package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.dto.UpdateProductRequest;
import com.example.ecommerce.service.TenantProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{tenantName}/products")
public class TenantProductController {

    TenantProductService tenantProductService;

    public TenantProductController(TenantProductService tenantProductService){
        this.tenantProductService = tenantProductService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createNewProduct(@PathVariable String tenantName, @RequestBody CreateProductRequest request,Authentication auth){
        ProductResponse response = tenantProductService.createProduct(tenantName,request,auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(@PathVariable String tenantName,
                                                             GetProductRequest request){

        Page<ProductResponse> products = tenantProductService.getProducts(tenantName,request);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id, @PathVariable String tenantName){
        ProductResponse response = tenantProductService.getProductById(id,tenantName);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable long id, @PathVariable String tenantName, @RequestBody UpdateProductRequest request,Authentication auth){
        ProductResponse response = tenantProductService.updateProduct(id,tenantName,request,auth);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id, @PathVariable String tenantName, Authentication auth) {

        tenantProductService.deleteProduct(id, tenantName,auth);

        return ResponseEntity.ok(
                "Product with id: " + id + " and tenant: " + tenantName + " deleted successfully"
        );
    }


}
