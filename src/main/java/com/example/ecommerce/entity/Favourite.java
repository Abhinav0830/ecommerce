package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "favourites")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;
}