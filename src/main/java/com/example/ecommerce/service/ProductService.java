package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.dto.UpdateProductRequest;
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
        product.setAvailableQuantity(request.getAvailableQuantity());
        product.setPrice(request.getPrice());
        product.setTenant(tenant);

        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice(), saved.getAvailableQuantity(), saved.getCategory());
    }

    @Transactional
    public Page<ProductResponse> getProducts(String tenantName,
                                             GetProductRequest request) {

        int page = (request.getPage()<0)?0: request.getPage();
        int size = (request.getSize()<=0) ? 0 : request.getSize();

        String sortBy = (request.getSortBy() == null || request.getSortBy().trim().isEmpty() ? "id" : request.getSortBy());
        String sortDir = (request.getSortDir() == null || request.getSortDir().trim().isEmpty() ? "asc" : request.getSortDir());
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);

        String categoryFilter = (request.getCategory() !=null && !request.getCategory().trim().isEmpty()) ? request.getCategory().trim() :null;
        String searchFilter = (request.getSearch()!=null && !request.getSearch().trim().isEmpty()) ? request.getSearch().trim() :null;

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

    @Transactional
    public ProductResponse getProductById(Long id,String tenantName){

        String cleanTenantName = tenantName.trim();

        Product product = productRepository.findByIdAndTenantName(id,cleanTenantName);
//                .orElseThrow(()->new IllegalArgumentException("Product Not Found with id: "+id+" for tenant "+tenantName);
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getAvailableQuantity(),
                product.getCategory()
        );
    }

    public ProductResponse updateProduct(Long id, String tenantName,  UpdateProductRequest request){
        String cleanTenantName = tenantName.trim();

        Product product = productRepository.findByIdAndTenantName(id,cleanTenantName);

        if(request.getName()!=null) product.setName(request.getName());
        if(request.getPrice()>=0) product.setPrice(request.getPrice());
        if(request.getAvailableQuantity()!=null) product.setAvailableQuantity(request.getAvailableQuantity());
        if(request.getCategory()!=null) product.setCategory(request.getCategory());

        Product updated = productRepository.save(product);

        return  new ProductResponse(
                updated.getId(),
                updated.getName(),
                updated.getPrice(),
                updated.getAvailableQuantity(),
                updated.getCategory()
        );


    }

    public void deleteProduct( Long id,String  tenantName){
        String cleanTenantName = tenantName.trim();

        Product product = productRepository.findByIdAndTenantName(id,cleanTenantName);

        productRepository.delete(product);

    }




}
