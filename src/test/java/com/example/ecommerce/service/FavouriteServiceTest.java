package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Favourite;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.FavouriteRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FavouriteService favouriteService;



    // addFavourite()


    @Test
    void addFavourite_shouldAddFavouriteSuccessfully() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(5)
                .deleted(false)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.existsByUserAndProduct(user, product))
                .thenReturn(false);

        favouriteService.addFavourite(authentication, 10L);

        verify(userRepository)
                .findByUsername("john");

        verify(productRepository)
                .findById(10L);

        verify(favouriteRepository)
                .existsByUserAndProduct(user, product);

        verify(favouriteRepository)
                .save(any(Favourite.class));
    }


    @Test
    void addFavourite_shouldThrowException_whenUserDoesNotExist() {

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.addFavourite(
                        authentication,
                        10L
                )
        );

        verify(userRepository)
                .findByUsername("john");

        verifyNoInteractions(productRepository, favouriteRepository);
    }


    @Test
    void addFavourite_shouldThrowException_whenProductDoesNotExist() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.addFavourite(
                        authentication,
                        10L
                )
        );

        verify(productRepository)
                .findById(10L);

        verifyNoInteractions(favouriteRepository);
    }


    @Test
    void addFavourite_shouldThrowException_whenFavouriteAlreadyExists() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.existsByUserAndProduct(user, product))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> favouriteService.addFavourite(
                        authentication,
                        10L
                )
        );

        verify(favouriteRepository)
                .existsByUserAndProduct(user, product);

        verify(favouriteRepository, never())
                .save(any(Favourite.class));
    }



    // removeFavourite()


    @Test
    void removeFavourite_shouldRemoveFavouriteSuccessfully() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .build();

        Favourite favourite = Favourite.builder()
                .id(100L)
                .user(user)
                .product(product)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(favourite));

        favouriteService.removeFavourite(authentication, 10L);

        verify(favouriteRepository)
                .findByUserAndProduct(user, product);

        verify(favouriteRepository)
                .delete(favourite);
    }


    @Test
    void removeFavourite_shouldThrowException_whenFavouriteDoesNotExist() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.removeFavourite(
                        authentication,
                        10L
                )
        );

        verify(favouriteRepository)
                .findByUserAndProduct(user, product);

        verify(favouriteRepository, never())
                .delete(any(Favourite.class));
    }


    // getFavourites()


    @Test
    void getFavourites_shouldReturnNonDeletedProducts() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(5)
                .category("Electronics")
                .deleted(false)
                .build();

        Favourite favourite = Favourite.builder()
                .id(100L)
                .user(user)
                .product(product)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favouriteRepository.findByUser(user))
                .thenReturn(List.of(favourite));

        List<ProductResponse> result =
                favouriteService.getFavourites(authentication);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(10L, result.get(0).getId());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals(50000, result.get(0).getPrice());
        assertEquals(5, result.get(0).getAvailableQuantity());
        assertEquals("Electronics", result.get(0).getCategory());

        verify(favouriteRepository)
                .findByUser(user);
    }


    @Test
    void getFavourites_shouldNotReturnDeletedProducts() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product deletedProduct = Product.builder()
                .id(10L)
                .name("Laptop")
                .price(50000)
                .availableQuantity(5)
                .category("Electronics")
                .deleted(true)
                .build();

        Favourite favourite = Favourite.builder()
                .id(100L)
                .user(user)
                .product(deletedProduct)
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favouriteRepository.findByUser(user))
                .thenReturn(List.of(favourite));

        List<ProductResponse> result =
                favouriteService.getFavourites(authentication);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(favouriteRepository)
                .findByUser(user);
    }


    @Test
    void getFavourites_shouldThrowException_whenUserDoesNotExist() {

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.getFavourites(authentication)
        );

        verify(userRepository)
                .findByUsername("john");

        verifyNoInteractions(favouriteRepository);
    }



    // isFavourite()


    @Test
    void isFavourite_shouldReturnTrue_whenFavouriteExists() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.existsByUserAndProduct(user, product))
                .thenReturn(true);

        boolean result =
                favouriteService.isFavourite(authentication, 10L);

        assertTrue(result);

        verify(favouriteRepository)
                .existsByUserAndProduct(user, product);
    }


    @Test
    void isFavourite_shouldReturnFalse_whenFavouriteDoesNotExist() {

        User user = User.builder()
                .id(1L)
                .username("john")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Laptop")
                .build();

        when(authentication.getName())
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.existsByUserAndProduct(user, product))
                .thenReturn(false);

        boolean result =
                favouriteService.isFavourite(authentication, 10L);

        assertFalse(result);

        verify(favouriteRepository)
                .existsByUserAndProduct(user, product);
    }
}