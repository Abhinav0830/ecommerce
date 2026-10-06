package com.example.ecommerce.service;


import com.example.ecommerce.dto.CreateTenantRequest;
import com.example.ecommerce.dto.TenantResponse;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
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
            throw new DuplicateResourceException("Tenant with name "+ tenantName + " already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setName(tenantName);

        Tenant saved = tenantRepository.save(tenant);

        return new TenantResponse(saved.getId(),saved.getName());
    }
    @Transactional
    public List<TenantResponse> getAllTenants(){
        List<Tenant> tenants = tenantRepository.findByDeletedFalse();
        List<TenantResponse> response = new ArrayList<>();
        for(Tenant tenant : tenants){
            response.add(new TenantResponse(tenant.getId(),tenant.getName()));
        }
        return response;
    }
    @Transactional
    public TenantResponse deleteTenant(Long id) {

        Tenant tenant = tenantRepository.findByIdAndDeletedFalse(id);
        if(tenant==null){
            throw new ResourceNotFoundException("Tenant not found with id: "+id);
        }

        tenant.setDeleted(true);
        tenantRepository.save(tenant);
        return new TenantResponse(tenant.getId(),tenant.getName());
    }
}
