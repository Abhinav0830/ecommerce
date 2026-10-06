package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.FavouriteService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> addFavourite(
            Authentication auth,
            @PathVariable Long productId) {

        favouriteService.addFavourite(auth, productId);

        return ResponseEntity.ok("Product added to favourites");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFavourite(
            Authentication auth,
            @PathVariable Long productId) {

        favouriteService.removeFavourite(auth, productId);

        return ResponseEntity.ok("Product removed from favourites");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getFavourites(
            Authentication auth) {

        return ResponseEntity.ok(
                favouriteService.getFavourites(auth)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Boolean> isFavourite(
            Authentication auth,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                favouriteService.isFavourite(auth, productId)
        );
    }
}