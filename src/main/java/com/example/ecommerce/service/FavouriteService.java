package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Favourite;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.FavouriteRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FavouriteService(
            FavouriteRepository favouriteRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.favouriteRepository = favouriteRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void addFavourite(Authentication auth, Long productId) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (favouriteRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product is already in favourites");
        }

        Favourite favourite = new Favourite();

        favourite.setUser(user);
        favourite.setProduct(product);

        favouriteRepository.save(favourite);
    }

    public void removeFavourite(Authentication auth, Long productId) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Favourite favourite = favouriteRepository
                .findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Product is not in favourites"));

        favouriteRepository.delete(favourite);
    }

    public List<ProductResponse> getFavourites(Authentication auth) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Favourite> favourites =
                favouriteRepository.findByUser(user);

        List<ProductResponse> products = new ArrayList<>();

        for (Favourite favourite : favourites) {

            Product product = favourite.getProduct();

            ProductResponse response = new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getAvailableQuantity(),
                    product.getCategory()
            );

            products.add(response);
        }

        return products;
    }

    public boolean isFavourite(Authentication auth, Long productId) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return favouriteRepository.existsByUserAndProduct(user, product);
    }
}