package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("SELECT p FROM Product p WHERE p.tenant.name = :tenantName " +
            "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))" +
            "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<Product> findByTenantAndFilters(@Param("tenantName") String tenantName,
                                         @Param("category") String categoryFilter,
                                         @Param("search") String searchFilter,
                                         Pageable pageable);


}
