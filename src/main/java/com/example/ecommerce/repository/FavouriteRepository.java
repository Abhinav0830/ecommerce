package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Favourite;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    Optional<Favourite> findByUserAndProduct(User user, Product product);

    List<Favourite> findByUser(User user);

    boolean existsByUserAndProduct(User user, Product product);
}