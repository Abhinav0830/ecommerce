package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateTenantUserRequest;
import com.example.ecommerce.dto.TenantUserResponse;
import com.example.ecommerce.service.TenantUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tenants")
public class TenantUserController {

    private final TenantUserService tenantUserService;

    public TenantUserController(TenantUserService tenantUserService) {
        this.tenantUserService = tenantUserService;
    }

    @PostMapping("/{tenantName}/users")
    public ResponseEntity<String> createTenantUser(
            @PathVariable String tenantName,
            @RequestBody CreateTenantUserRequest request
    ) {

        tenantUserService.createTenantUser(tenantName, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Tenant user created successfully");
    }

    @GetMapping("/{tenantName}/users")
    public ResponseEntity<List<TenantUserResponse>> getTenantUsers(
            @PathVariable String tenantName) {

        return ResponseEntity.ok(
                tenantUserService.getTenantUsers(tenantName)
        );
    }

    @PutMapping("/{tenantName}/users/{userId}")
    public ResponseEntity<String> deleteTenantUser(
            @PathVariable String tenantName,
            @PathVariable Long userId) {

        tenantUserService.deleteTenantUser(
                tenantName,
                userId
        );

        return ResponseEntity.ok(
                "Tenant user deleted successfully"
        );
    }
}