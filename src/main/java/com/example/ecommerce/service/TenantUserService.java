package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateTenantUserRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.TenantRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantUserService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Value("${keycloak.realm}")
    private String realm;

    public TenantUserService(
            Keycloak keycloak,
            UserRepository userRepository,
            TenantRepository tenantRepository
    ) {
        this.keycloak = keycloak;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    public void createTenantUser(
            String tenantName,
            CreateTenantUserRequest request
    ) {

        // 1. Find tenant
        Tenant tenant = tenantRepository.findByName(tenantName)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found"));

        // 2. Check if username already exists in our database
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 3. Create Keycloak user
        UserRepresentation keycloakUser = new UserRepresentation();

        keycloakUser.setUsername(request.getUsername());
        keycloakUser.setEnabled(true);

        CredentialRepresentation credential =
                new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        keycloakUser.setCredentials(List.of(credential));

        Response response = keycloak
                .realm(realm)
                .users()
                .create(keycloakUser);

        if (response.getStatus() != 201) {
            throw new RuntimeException(
                    "Failed to create Keycloak user: "
                            + response.getStatus()
            );
        }

        // 4. Get the newly created Keycloak user's ID
        String keycloakUserId = response
                .getLocation()
                .getPath()
                .replaceAll(".*/([^/]+)$", "$1");

        // 5. Get TENANT realm role from Keycloak
        RoleRepresentation tenantRole = keycloak
                .realm(realm)
                .roles()
                .get("TENANT")
                .toRepresentation();

        // 6. Assign TENANT role to Keycloak user
        keycloak
                .realm(realm)
                .users()
                .get(keycloakUserId)
                .roles()
                .realmLevel()
                .add(List.of(tenantRole));

        // 7. Create application user in database
        User user = new User();

        user.setUsername(request.getUsername());
        user.setRole(Role.TENANT);
        user.setTenant(tenant);

        userRepository.save(user);
    }
}