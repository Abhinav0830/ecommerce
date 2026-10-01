package com.example.ecommerce.service;

import com.example.ecommerce.dto.UserSignUpRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;

    public UserService(UserRepository userRepository , Keycloak keycloak){
        this.userRepository = userRepository;
        this.keycloak=keycloak;
    }

    public User signUp(UserSignUpRequest request){
        if(userRepository.findByUsername(request.getUsername())!=null){
            throw new RuntimeException("User already exists");
        }
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(request.getUsername());
        keycloakUser.setEnabled(true);

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(request.getPassword());
        credentials.setTemporary(false);

        keycloakUser.setCredentials(List.of(credentials));

        RealmResource realm = keycloak.realm("ecommerce");
        Response response = realm.users().create(keycloakUser);
        if(response.getStatus()!=201){
            throw new RuntimeException("Failed to create User");
        }
        String keyCloakUserId = CreatedResponseUtil.getCreatedId(response);

        RoleRepresentation userRole = realm.roles().get("USER").toRepresentation();
        realm.users().get(keyCloakUserId).roles().realmLevel().add(List.of(userRole));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(Role.USER);
        user.setTenant(null);
        return userRepository.save(user);

    }



}
