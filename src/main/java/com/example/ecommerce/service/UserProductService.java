package com.example.ecommerce.service;

import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserProductService {

    private final ProductRepository productRepository;

    public UserProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Transactional
    public Page<ProductResponse> getProducts(GetProductRequest request) {

        int page = request.getPage() < 0 ? 0 : request.getPage();

        int size = request.getSize() <= 0
                ? 10
                : request.getSize();

        String sortBy =
                request.getSortBy() == null ||
                        request.getSortBy().trim().isEmpty()
                        ? "id"
                        : request.getSortBy();

        String sortDir =
                request.getSortDir() == null ||
                        request.getSortDir().trim().isEmpty()
                        ? "asc"
                        : request.getSortDir();

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        String category =
                request.getCategory() != null &&
                        !request.getCategory().trim().isEmpty()
                        ? request.getCategory().trim()
                        : null;

        String search =
                request.getSearch() != null &&
                        !request.getSearch().trim().isEmpty()
                        ? request.getSearch().trim()
                        : null;

        Page<Product> productPage =
                productRepository.findAllWithFiltersAndDeletedFalse(
                        category,
                        search,
                        pageable
                );

        List<ProductResponse> responses = new ArrayList<>();

        for (Product product : productPage.getContent()) {

            responses.add(
                    new ProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getAvailableQuantity(),
                            product.getCategory()
                    )
            );
        }

        return new PageImpl<>(
                responses,
                pageable,
                productPage.getTotalElements()
        );
    }
}