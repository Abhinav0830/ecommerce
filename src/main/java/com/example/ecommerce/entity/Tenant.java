package com.example.ecommerce.entity;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String name;

//    @Enumerated(EnumType.STRING)
//    private Role role;
    private boolean deleted = false;

    @OneToMany(mappedBy = "tenant")
    private List<Product> products;

    @OneToMany(mappedBy = "tenant")
    private List<User> users;
}
