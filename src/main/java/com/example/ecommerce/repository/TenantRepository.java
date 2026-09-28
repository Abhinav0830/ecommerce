package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant,Long> {

    boolean existsByName(String name);

    Tenant findByName(String tenantName);
}
