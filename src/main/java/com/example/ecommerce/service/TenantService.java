package com.example.ecommerce.service;


import com.example.ecommerce.controller.TenantController;
import com.example.ecommerce.dto.CreateTenantRequest;
import com.example.ecommerce.dto.TenantResponse;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    public  TenantService(TenantRepository tenantRepository){
        this.tenantRepository = tenantRepository;
    }
    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request){
        String tenantName = request.getName();

        if(tenantRepository.existsByName(tenantName)){
            throw new IllegalArgumentException("Tenant with name "+ tenantName + " already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setName(tenantName);

        Tenant saved = tenantRepository.save(tenant);

        return new TenantResponse(saved.getId(),saved.getName());
    }
    @Transactional
    public List<TenantResponse> getAllTenants(){
        List<Tenant> tenants = tenantRepository.findAll();
        List<TenantResponse> response = new ArrayList<>();
        for(Tenant tenant : tenants){
            response.add(new TenantResponse(tenant.getId(),tenant.getName()));
        }
        return response;
    }

    public TenantResponse deleteTenant(Long id) {
        if(!tenantRepository.existsById(id)) {
            throw new IllegalArgumentException("Tenant not found with id " + id);
        }
        Optional<Tenant> tenant = tenantRepository.findById(id);
        tenantRepository.deleteById(id);
        return new TenantResponse(tenant.get().getId(),tenant.get().getName());
    }
}
