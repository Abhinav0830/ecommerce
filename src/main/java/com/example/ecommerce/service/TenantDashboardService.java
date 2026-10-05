package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TenantDashboardService {

    private final UserRepository userRepository;

    public TenantDashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getMyTenant(Authentication auth) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTenant() == null) {
            throw new RuntimeException("User has no tenant");
        }

        return user.getTenant().getName();
    }
}
