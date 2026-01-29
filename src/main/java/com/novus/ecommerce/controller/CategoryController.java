package com.novus.ecommerce.controller;

import com.novus.ecommerce.dto.entity.CategoryDto;
import com.novus.ecommerce.dto.response.Response;
import com.novus.ecommerce.entity.Category;
import com.novus.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<List<CategoryDto>> getAllCategories() {
        log.info("GET /api/categories - Fetching all categories");
        List<CategoryDto> categories = categoryService.findAll().stream()
                .map(categoryService::mapToDto)
                .toList();
        log.debug("Found {} categories", categories.size());

        return Response.<List<CategoryDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Categories retrieved successfully")
                .data(categories)
                .build();
    }

    @PostMapping("/admin/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<?> addCategory(@RequestBody Category category) {
        log.info("POST /api/categories/admin/add - Creating category: {}", category.getName());
        categoryService.addCategory(category);
        log.info("Category '{}' created successfully", category.getName());

        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("Category created successfully")
                .build();
    }
}