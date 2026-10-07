package com.example.ecommerce.service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getCategories_shouldReturnCategories() {

        Category category1 = new Category();
        Category category2 = new Category();

        List<Category> categories = List.of(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getCategories();

        assertEquals(categories, result);

        verify(categoryRepository).findAll();
    }

    @Test
    void getCategories_shouldReturnEmptyList_whenNoCategoriesExist() {

        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> result = categoryService.getCategories();

        assertTrue(result.isEmpty());

        verify(categoryRepository).findAll();
    }
}