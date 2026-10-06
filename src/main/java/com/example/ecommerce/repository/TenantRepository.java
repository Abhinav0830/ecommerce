package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant,Long> {

    boolean existsByName(String name);

    Optional<Tenant> findByName(String tenantName);

    //Optional<Tenant> findByIdAndIsDeletedFalse(Long id);

    Tenant findByIdAndDeletedFalse(Long id);

    List<Tenant> findByDeletedFalse();
}
