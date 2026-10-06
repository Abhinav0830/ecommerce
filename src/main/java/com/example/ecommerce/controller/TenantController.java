package com.example.ecommerce.controller;


import com.example.ecommerce.dto.CreateTenantRequest;
import com.example.ecommerce.dto.TenantResponse;
import com.example.ecommerce.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tenants")
public class TenantController {
    private final TenantService tenantService;

    public TenantController (TenantService tenantService){
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant (@Valid @RequestBody CreateTenantRequest request){
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants(){
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTenant(@PathVariable Long id){
        TenantResponse res = tenantService.deleteTenant(id);
        return ResponseEntity.ok().body("Deleted tenant "+res.getName()+ " successfully");
    }

}
