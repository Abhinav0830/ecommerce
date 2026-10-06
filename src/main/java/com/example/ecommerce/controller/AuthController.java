package com.example.ecommerce.controller;

import com.example.ecommerce.dto.UserSignUpRequest;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/auth")
public class AuthController {
    UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequest request){
        System.out.println("SIGNUP CONTROLLER HIT");
        return ResponseEntity.ok(userService.signUp(request));
    }
}
