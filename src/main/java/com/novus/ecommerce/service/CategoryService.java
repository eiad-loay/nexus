package com.novus.ecommerce.service;

import com.novus.ecommerce.dto.entity.CategoryDto;
import com.novus.ecommerce.entity.Category;
import com.novus.ecommerce.exception.custom.EntityNotFoundException;
import com.novus.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id " + id + " not found"));
    }

    @Cacheable(value = "categories", key = "#name")
    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Category with name " + name + " not found"));
    }

    @Cacheable(value = "categories", key = "'all'")
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public CategoryDto mapToDto(Category category) {
        return new CategoryDto(
                category.getName(),
                baseUrl + "/api/products?category=" + category.getName().toLowerCase());
    }
}