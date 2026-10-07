package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.dto.GetProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.dto.UpdateProductRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.TenantRepository;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TenantProductService tenantProductService;



    // createProduct()


    @Test
    void createProduct_shouldCreateProductAndCategorySuccessfully() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .id(1L)
                .username("john")
                .tenant(tenant)
                .build();

        CreateProductRequest request = CreateProductRequest.builder()
                .name("Laptop")
                .category("Electronics")
                .availableQuantity(10)
                .price(50000)
                .build();

        Product savedProduct = Product.builder()
                .id(10L)
                .name("Laptop")
                .category("Electronics")
                .availableQuantity(10)
                .price(50000)
                .tenant(tenant)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response =
                tenantProductService.createProduct(
                        "Tenant A",
                        request,
                        authentication
                );

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(50000, response.getPrice());
        assertEquals(10, response.getAvailableQuantity());
        assertEquals("Electronics", response.getCategory());

        verify(categoryRepository)
                .save(any(Category.class));

        verify(productRepository)
                .save(any(Product.class));
    }


    @Test
    void createProduct_shouldNotCreateCategory_whenCategoryAlreadyExists() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .id(1L)
                .username("john")
                .tenant(tenant)
                .build();

        CreateProductRequest request = CreateProductRequest.builder()
                .name("Laptop")
                .category("Electronics")
                .availableQuantity(10)
                .price(50000)
                .build();

        Product savedProduct = Product.builder()
                .id(10L)
                .name("Laptop")
                .category("Electronics")
                .availableQuantity(10)
                .price(50000)
                .tenant(tenant)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(true);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response =
                tenantProductService.createProduct(
                        "Tenant A",
                        request,
                        authentication
                );

        assertNotNull(response);

        verify(categoryRepository, never())
                .save(any(Category.class));

        verify(productRepository)
                .save(any(Product.class));
    }


    @Test
    void createProduct_shouldThrowException_whenTenantDoesNotExist() {

        CreateProductRequest request = CreateProductRequest.builder()
                .name("Laptop")
                .category("Electronics")
                .availableQuantity(10)
                .price(50000)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantProductService.createProduct(
                        "Tenant A",
                        request,
                        authentication
                )
        );

        verifyNoInteractions(
                userRepository,
                productRepository,
                categoryRepository
        );
    }



    // getProducts()


    @Test
    void getProducts_shouldReturnProductsWithPagination() {

        GetProductRequest request = GetProductRequest.builder()
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDir("asc")
                .category("Electronics")
                .search("lap")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(5)
                .category("Electronics")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findByTenantAndDeletedFalseAndFilters(
                eq("Tenant A"),
                eq("Electronics"),
                eq("lap"),
                any(PageRequest.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                tenantProductService.getProducts(
                        "Tenant A",
                        request
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("Laptop", result.getContent().get(0).getName());

        verify(productRepository)
                .findByTenantAndDeletedFalseAndFilters(
                        eq("Tenant A"),
                        eq("Electronics"),
                        eq("lap"),
                        any(PageRequest.class)
                );
    }


    @Test
    void getProducts_shouldUseDefaultValues_whenPaginationAndFiltersAreInvalid() {

        GetProductRequest request = GetProductRequest.builder()
                .page(-1)
                .size(0)
                .sortBy(" ")
                .sortDir(" ")
                .category(" ")
                .search(" ")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findByTenantAndDeletedFalseAndFilters(
                eq("Tenant A"),
                isNull(),
                isNull(),
                any(PageRequest.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                tenantProductService.getProducts(
                        "Tenant A",
                        request
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository)
                .findByTenantAndDeletedFalseAndFilters(
                        eq("Tenant A"),
                        isNull(),
                        isNull(),
                        any(PageRequest.class)
                );
    }


    @Test
    void getProducts_shouldUseDescendingSort_whenSortDirectionIsDesc() {

        GetProductRequest request = GetProductRequest.builder()
                .page(0)
                .size(5)
                .sortBy("price")
                .sortDir("desc")
                .build();

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findByTenantAndDeletedFalseAndFilters(
                eq("Tenant A"),
                isNull(),
                isNull(),
                any(PageRequest.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                tenantProductService.getProducts(
                        "Tenant A",
                        request
                );

        assertNotNull(result);

        verify(productRepository)
                .findByTenantAndDeletedFalseAndFilters(
                        eq("Tenant A"),
                        isNull(),
                        isNull(),
                        any(PageRequest.class)
                );
    }



    // getProductById()


    @Test
    void getProductById_shouldReturnProduct() {

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(5)
                .category("Electronics")
                .build();

        when(productRepository.findByIdAndTenantNameAndDeletedFalse(
                10L,
                "Tenant A"
        )).thenReturn(product);

        ProductResponse response =
                tenantProductService.getProductById(
                        10L,
                        " Tenant A "
                );

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(50000, response.getPrice());
        assertEquals(5, response.getAvailableQuantity());
        assertEquals("Electronics", response.getCategory());

        verify(productRepository)
                .findByIdAndTenantNameAndDeletedFalse(
                        10L,
                        "Tenant A"
                );
    }


    @Test
    void getProductById_shouldThrowException_whenProductDoesNotExist() {

        when(productRepository.findByIdAndTenantNameAndDeletedFalse(
                10L,
                "Tenant A"
        )).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantProductService.getProductById(
                        10L,
                        " Tenant A "
                )
        );

        verify(productRepository)
                .findByIdAndTenantNameAndDeletedFalse(
                        10L,
                        "Tenant A"
                );
    }



    // updateProduct()


    @Test
    void updateProduct_shouldUpdateAllFields() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .id(1L)
                .username("john")
                .tenant(tenant)
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Old Laptop")
                .price(40000)
                .availableQuantity(5)
                .category("Old Category")
                .tenant(tenant)
                .build();

        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("New Laptop")
                .price(60000)
                .availableQuantity(20)
                .category("Electronics")
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndTenantName(
                10L,
                "Tenant A"
        )).thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        ProductResponse response =
                tenantProductService.updateProduct(
                        10L,
                        " Tenant A ",
                        request,
                        authentication
                );

        assertEquals("New Laptop", response.getName());
        assertEquals(60000, response.getPrice());
        assertEquals(20, response.getAvailableQuantity());
        assertEquals("Electronics", response.getCategory());

        verify(productRepository)
                .save(product);
    }


    @Test
    void updateProduct_shouldKeepExistingValues_whenOptionalFieldsAreNotProvided() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .username("john")
                .tenant(tenant)
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(10)
                .category("Electronics")
                .build();

        UpdateProductRequest request = UpdateProductRequest.builder()
                .name(null)
                .price(-1)
                .availableQuantity(null)
                .category(null)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndTenantName(
                10L,
                "Tenant A"
        )).thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        ProductResponse response =
                tenantProductService.updateProduct(
                        10L,
                        "Tenant A",
                        request,
                        authentication
                );

        assertEquals("Laptop", response.getName());
        assertEquals(50000, response.getPrice());
        assertEquals(10, response.getAvailableQuantity());
        assertEquals("Electronics", response.getCategory());

        verify(productRepository)
                .save(product);
    }


    @Test
    void updateProduct_shouldThrowException_whenProductDoesNotExist() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .username("john")
                .tenant(tenant)
                .build();

        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("Laptop")
                .price(50000)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndTenantName(
                10L,
                "Tenant A"
        )).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantProductService.updateProduct(
                        10L,
                        "Tenant A",
                        request,
                        authentication
                )
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }



    // deleteProduct()


    @Test
    void deleteProduct_shouldSoftDeleteProduct() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .username("john")
                .tenant(tenant)
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .deleted(false)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndTenantNameAndDeletedFalse(
                10L,
                "Tenant A"
        )).thenReturn(product);

        tenantProductService.deleteProduct(
                10L,
                " Tenant A ",
                authentication
        );

        assertTrue(product.isDeleted());

        verify(productRepository)
                .save(product);
    }


    @Test
    void deleteProduct_shouldThrowException_whenProductDoesNotExist() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .username("john")
                .tenant(tenant)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findByIdAndTenantNameAndDeletedFalse(
                10L,
                "Tenant A"
        )).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantProductService.deleteProduct(
                        10L,
                        "Tenant A",
                        authentication
                )
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }



    // validateTenantAccess()


    @Test
    void validateTenantAccess_shouldPass_whenUserBelongsToTenant() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .username("john")
                .tenant(tenant)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(
                () -> tenantProductService.validateTenantAccess(
                        authentication,
                        "Tenant A"
                )
        );

        verify(userRepository)
                .findByUsername("john");
    }


    @Test
    void validateTenantAccess_shouldThrowException_whenUserDoesNotExist() {

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> tenantProductService.validateTenantAccess(
                        authentication,
                        "Tenant A"
                )
        );
    }


    @Test
    void validateTenantAccess_shouldThrowException_whenUserHasNoTenant() {

        User user = User.builder()
                .username("john")
                .tenant(null)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        assertThrows(
                RuntimeException.class,
                () -> tenantProductService.validateTenantAccess(
                        authentication,
                        "Tenant A"
                )
        );
    }


    @Test
    void validateTenantAccess_shouldThrowException_whenUserBelongsToDifferentTenant() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant B")
                .build();

        User user = User.builder()
                .username("john")
                .tenant(tenant)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        assertThrows(
                RuntimeException.class,
                () -> tenantProductService.validateTenantAccess(
                        authentication,
                        "Tenant A"
                )
        );
    }
}