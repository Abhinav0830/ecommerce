package com.example.ecommerce.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/auth")
    public String debug(Authentication authentication) {

        System.out.println("Username: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());

        return authentication.getAuthorities().toString();
    }
}