package com.example.ecommerce.controller;

import com.example.ecommerce.service.TenantDashboardService;
import com.example.ecommerce.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tenant")
public class TenantDashboardController {

    private final TenantDashboardService tenantDashboardService;

    public TenantDashboardController(TenantDashboardService tenantDashboardService) {
        this.tenantDashboardService = tenantDashboardService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyTenant(Authentication auth) {

        String tenantName = tenantDashboardService.getMyTenant(auth);

        return ResponseEntity.ok(
                Map.of("tenantName", tenantName)
        );
    }
}