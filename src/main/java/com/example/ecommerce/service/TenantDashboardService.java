package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TenantDashboardService {

    private final UserRepository userRepository;

    public TenantDashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    public String getMyTenant(Authentication auth) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getTenant() == null) {
            throw new ResourceNotFoundException("User has no tenant");
        }

        return user.getTenant().getName();
    }
}
