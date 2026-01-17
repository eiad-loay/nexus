package com.nexus.ecommerce.service;

import com.nexus.ecommerce.entity.Category;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id " + id + " not found")
        );
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Category with name " + name + " not found")
        );
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
