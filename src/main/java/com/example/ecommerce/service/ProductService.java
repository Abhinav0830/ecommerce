package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;

    public ProductService(ProductRepository productRepository, TenantRepository tenantRepository) {
        this.productRepository = productRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public ProductResponse createProduct(String tenantName, CreateProductRequest request) {

        Tenant tenant = tenantRepository.findByName(tenantName);

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setAvailableQuantity(request.getAvailabaleQuantity());
        product.setPrice(request.getPrice());
        product.setTenant(tenant);

        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice(), saved.getAvailableQuantity(), saved.getCategory());
    }

    @Transactional
    public Page<ProductResponse> getProducts(String tenantName,
                                             String category,
                                             String search,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);

        String categoryFilter = (category !=null && !category.trim().isEmpty()) ? category.trim() :null;
        String searchFilter = (search !=null && !search.trim().isEmpty()) ? search.trim() :null;

        Page<Product> productPage = productRepository.findByTenantAndFilters(tenantName,categoryFilter,searchFilter,pageable);

        List<ProductResponse> responses = new ArrayList<>();
        for(Product p : productPage.getContent()){
            responses.add(
                    new ProductResponse(
                            p.getId(),
                            p.getName(),
                            p.getPrice(),
                            p.getAvailableQuantity()
                            ,p.getCategory()
                    )
            );
        }
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }



}
