package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateTenantRequest;
import com.example.ecommerce.dto.TenantResponse;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;


    @Test
    void createTenant_shouldCreateTenantSuccessfully() {

        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Tenant A")
                .build();

        Tenant savedTenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        when(tenantRepository.existsByName("Tenant A"))
                .thenReturn(false);

        when(tenantRepository.save(any(Tenant.class)))
                .thenReturn(savedTenant);

        TenantResponse response = tenantService.createTenant(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tenant A", response.getName());

        verify(tenantRepository).existsByName("Tenant A");
        verify(tenantRepository).save(any(Tenant.class));
    }


    @Test
    void createTenant_shouldThrowException_whenTenantAlreadyExists() {

        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Tenant A")
                .build();

        when(tenantRepository.existsByName("Tenant A"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> tenantService.createTenant(request)
        );

        verify(tenantRepository).existsByName("Tenant A");

        verify(tenantRepository, never())
                .save(any(Tenant.class));
    }


    @Test
    void getAllTenants_shouldReturnAllActiveTenants() {

        Tenant tenant1 = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        Tenant tenant2 = Tenant.builder()
                .id(2L)
                .name("Tenant B")
                .build();

        when(tenantRepository.findByDeletedFalse())
                .thenReturn(List.of(tenant1, tenant2));

        List<TenantResponse> response =
                tenantService.getAllTenants();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals("Tenant A", response.get(0).getName());

        assertEquals(2L, response.get(1).getId());
        assertEquals("Tenant B", response.get(1).getName());

        verify(tenantRepository).findByDeletedFalse();
    }


    @Test
    void getAllTenants_shouldReturnEmptyList_whenNoTenantsExist() {

        when(tenantRepository.findByDeletedFalse())
                .thenReturn(List.of());

        List<TenantResponse> response =
                tenantService.getAllTenants();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(tenantRepository).findByDeletedFalse();
    }


    @Test
    void deleteTenant_shouldSoftDeleteTenantSuccessfully() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .deleted(false)
                .build();

        when(tenantRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(tenant);

        when(tenantRepository.save(any(Tenant.class)))
                .thenReturn(tenant);

        TenantResponse response =
                tenantService.deleteTenant(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tenant A", response.getName());

        assertTrue(tenant.isDeleted());

        verify(tenantRepository)
                .findByIdAndDeletedFalse(1L);

        verify(tenantRepository)
                .save(tenant);
    }


    @Test
    void deleteTenant_shouldThrowException_whenTenantDoesNotExist() {

        when(tenantRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantService.deleteTenant(1L)
        );

        verify(tenantRepository)
                .findByIdAndDeletedFalse(1L);

        verify(tenantRepository, never())
                .save(any(Tenant.class));
    }
}