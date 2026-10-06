package com.example.ecommerce.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {

    @NotBlank
    @Size(min=2,max=30)
    private String username;

    @NotBlank
    @Size(min=2,max=30)
    private String password;
//    private long tenantId;

}
