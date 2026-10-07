package com.example.ecommerce.dto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantResponse {

    private long id;
    private String name;
}
