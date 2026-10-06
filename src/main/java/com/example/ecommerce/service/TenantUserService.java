package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateTenantUserRequest;
import com.example.ecommerce.dto.TenantUserResponse;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.Tenant;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.TenantRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Transactional
    public void createTenantUser(
            String tenantName,
            CreateTenantUserRequest request
    ) {

        // 1. Find tenant
        Tenant tenant = tenantRepository.findByName(tenantName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tenant not found"));

        // 2. Check if username already exists in our database
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        // 3. Create Keycloak user
        UserRepresentation keycloakUser = new UserRepresentation();

        keycloakUser.setUsername(request.getUsername().toLowerCase());
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
    @Transactional
    public List<TenantUserResponse> getTenantUsers(String tenantName) {

        Tenant tenant = tenantRepository.findByName(tenantName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tenant not found"));

        List<User> users =
                userRepository.findByTenantAndRoleAndActiveTrue(
                        tenant,
                        Role.TENANT
                );

        List<TenantUserResponse> response = new ArrayList<>();

        for (User user : users) {
            TenantUserResponse userResponse =
                    new TenantUserResponse(
                            user.getId(),
                            user.getUsername()
                    );

            response.add(userResponse);
        }

        return response;
    }
    @Transactional
    public void deleteTenantUser(
            String tenantName,
            Long userId
    ) {
       // System.out.println("Starting disable");
        Tenant tenant = tenantRepository.findByName(tenantName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tenant not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getRole() != Role.TENANT) {
            throw new RuntimeException("User is not a tenant user");
        }
        System.out.println("User is a tenant");
        // the user belongs to the requested tenant
        if (user.getTenant() == null ||
                !user.getTenant().getId().equals(tenant.getId())) {

            throw new ResourceNotFoundException(
                    "User does not belong to this tenant"
            );
        }

        // Find the user in Keycloak
        List<UserRepresentation> keycloakUsers = keycloak
                .realm(realm)
                .users()
                .search(user.getUsername());





        //debugging
        System.out.println(
                "Searching Keycloak for: " + user.getUsername()
        );

        System.out.println(
                "Keycloak users found: " + keycloakUsers.size()
        );

        for (UserRepresentation u : keycloakUsers) {
            System.out.println(
                    "Found Keycloak username: " + u.getUsername()
            );
        }
        //ends

        UserRepresentation keycloakUser = null;
        for (UserRepresentation u : keycloakUsers) {

            if (user.getUsername().equalsIgnoreCase(u.getUsername())) {
                keycloakUser = u;
                break;
            }
        }

        if (keycloakUser == null) {
            throw new ResourceNotFoundException("User not found in Keycloak");
        }

        keycloakUser.setEnabled(false);
        System.out.println("Keycloak user false");
        // Delete Keycloak user first
        keycloak
                .realm(realm)
                .users()
                .get(keycloakUser.getId())
                .update(keycloakUser);
        System.out.println("Keycloak db update");


        // Only delete DB user after Keycloak deletion succeeds
        user.setActive(false);
        userRepository.save(user);
        System.out.println("User db update");
    }
}