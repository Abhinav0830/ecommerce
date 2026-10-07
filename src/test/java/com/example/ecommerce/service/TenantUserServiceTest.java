package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateTenantUserRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.TenantRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantUserServiceTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private RoleScopeResource roleScopeResource;

    @Mock
    private Response response;

    @InjectMocks
    private TenantUserService tenantUserService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                tenantUserService,
                "realm",
                "test-realm"
        );
    }


    // createTenantUser()


    @Test
    void createTenantUser_shouldCreateUserSuccessfully() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        CreateTenantUserRequest request =
                CreateTenantUserRequest.builder()
                        .username("John")
                        .password("password123")
                        .build();

        RoleRepresentation tenantRole =
                new RoleRepresentation();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByUsername("John"))
                .thenReturn(Optional.empty());

        when(keycloak.realm("test-realm"))
                .thenReturn(realmResource);

        when(realmResource.users())
                .thenReturn(usersResource);

        when(usersResource.create(any(UserRepresentation.class)))
                .thenReturn(response);

        when(response.getStatus())
                .thenReturn(201);

        when(response.getLocation())
                .thenReturn(
                        URI.create(
                                "http://localhost/admin/realms/test-realm/users/abc123"
                        )
                );

        when(realmResource.roles())
                .thenReturn(rolesResource);

        when(rolesResource.get("TENANT"))
                .thenReturn(roleResource);

        when(roleResource.toRepresentation())
                .thenReturn(tenantRole);

        when(usersResource.get("abc123"))
                .thenReturn(userResource);

        when(userResource.roles())
                .thenReturn(roleMappingResource);

        when(roleMappingResource.realmLevel())
                .thenReturn(roleScopeResource);

        tenantUserService.createTenantUser(
                "Tenant A",
                request
        );

        verify(tenantRepository)
                .findByName("Tenant A");

        verify(userRepository)
                .findByUsername("John");

        verify(usersResource)
                .create(any(UserRepresentation.class));

        verify(roleScopeResource)
                .add(List.of(tenantRole));

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void createTenantUser_shouldThrowException_whenTenantDoesNotExist() {

        CreateTenantUserRequest request =
                CreateTenantUserRequest.builder()
                        .username("John")
                        .password("password123")
                        .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantUserService.createTenantUser(
                        "Tenant A",
                        request
                )
        );

        verify(tenantRepository)
                .findByName("Tenant A");

        verifyNoInteractions(
                userRepository,
                keycloak
        );
    }

    @Test
    void createTenantUser_shouldThrowException_whenUsernameAlreadyExists() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User existingUser = User.builder()
                .username("John")
                .build();

        CreateTenantUserRequest request =
                CreateTenantUserRequest.builder()
                        .username("John")
                        .password("password123")
                        .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByUsername("John"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                DuplicateResourceException.class,
                () -> tenantUserService.createTenantUser(
                        "Tenant A",
                        request
                )
        );

        verify(userRepository)
                .findByUsername("John");

        verifyNoInteractions(keycloak);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void createTenantUser_shouldThrowException_whenKeycloakCreationFails() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        CreateTenantUserRequest request =
                CreateTenantUserRequest.builder()
                        .username("John")
                        .password("password123")
                        .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByUsername("John"))
                .thenReturn(Optional.empty());

        when(keycloak.realm("test-realm"))
                .thenReturn(realmResource);

        when(realmResource.users())
                .thenReturn(usersResource);

        when(usersResource.create(any(UserRepresentation.class)))
                .thenReturn(response);

        when(response.getStatus())
                .thenReturn(400);

        assertThrows(
                RuntimeException.class,
                () -> tenantUserService.createTenantUser(
                        "Tenant A",
                        request
                )
        );

        verify(usersResource)
                .create(any(UserRepresentation.class));

        verify(userRepository, never())
                .save(any(User.class));
    }


    // getTenantUsers()


    @Test
    void getTenantUsers_shouldReturnTenantUsers() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user1 = User.builder()
                .id(10L)
                .username("john")
                .role(Role.TENANT)
                .tenant(tenant)
                .build();

        User user2 = User.builder()
                .id(11L)
                .username("alice")
                .role(Role.TENANT)
                .tenant(tenant)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByTenantAndRoleAndActiveTrue(
                tenant,
                Role.TENANT
        )).thenReturn(List.of(user1, user2));

        var result =
                tenantUserService.getTenantUsers("Tenant A");

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10L, result.get(0).getId());
        assertEquals("john", result.get(0).getUsername());

        assertEquals(11L, result.get(1).getId());
        assertEquals("alice", result.get(1).getUsername());

        verify(tenantRepository)
                .findByName("Tenant A");

        verify(userRepository)
                .findByTenantAndRoleAndActiveTrue(
                        tenant,
                        Role.TENANT
                );
    }

    @Test
    void getTenantUsers_shouldReturnEmptyList_whenNoUsersExist() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByTenantAndRoleAndActiveTrue(
                tenant,
                Role.TENANT
        )).thenReturn(List.of());

        var result =
                tenantUserService.getTenantUsers("Tenant A");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository)
                .findByTenantAndRoleAndActiveTrue(
                        tenant,
                        Role.TENANT
                );
    }

    @Test
    void getTenantUsers_shouldThrowException_whenTenantDoesNotExist() {

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantUserService.getTenantUsers("Tenant A")
        );

        verify(tenantRepository)
                .findByName("Tenant A");

        verifyNoInteractions(userRepository);
    }


    // deleteTenantUser()


    @Test
    void deleteTenantUser_shouldThrowException_whenUserIsNotTenantUser() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .id(10L)
                .username("john")
                .role(Role.ADMIN)
                .tenant(tenant)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        assertThrows(
                RuntimeException.class,
                () -> tenantUserService.deleteTenantUser(
                        "Tenant A",
                        10L
                )
        );

        verify(userRepository)
                .findById(10L);

        verifyNoInteractions(keycloak);
    }

    @Test
    void deleteTenantUser_shouldThrowException_whenUserBelongsToDifferentTenant() {

        Tenant tenantA = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        Tenant tenantB = Tenant.builder()
                .id(2L)
                .name("Tenant B")
                .build();

        User user = User.builder()
                .id(10L)
                .username("john")
                .role(Role.TENANT)
                .tenant(tenantB)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenantA));

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantUserService.deleteTenantUser(
                        "Tenant A",
                        10L
                )
        );

        verifyNoInteractions(keycloak);
    }

    @Test
    void deleteTenantUser_shouldThrowException_whenKeycloakUserDoesNotExist() {

        Tenant tenant = Tenant.builder()
                .id(1L)
                .name("Tenant A")
                .build();

        User user = User.builder()
                .id(10L)
                .username("john")
                .role(Role.TENANT)
                .tenant(tenant)
                .build();

        when(tenantRepository.findByName("Tenant A"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        when(keycloak.realm("test-realm"))
                .thenReturn(realmResource);

        when(realmResource.users())
                .thenReturn(usersResource);

        when(usersResource.search("john"))
                .thenReturn(List.of());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantUserService.deleteTenantUser(
                        "Tenant A",
                        10L
                )
        );

        verify(usersResource)
                .search("john");

        verify(userRepository, never())
                .save(any(User.class));
    }
}