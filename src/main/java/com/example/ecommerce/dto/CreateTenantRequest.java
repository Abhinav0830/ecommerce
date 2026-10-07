package com.example.ecommerce.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {

    @NotBlank(message = "Tenant name is required")
    @Size(min = 2, max = 50, message = "Tenant name must be between 2 and 50 characters")
    private String name;
}
