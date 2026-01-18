package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.CategoryDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.Category;
import com.nexus.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<?> getAllCategories() {
        List<CategoryDto> categories = categoryService.findAll().stream().map(categoryService::mapToDto).collect(Collectors.toList());
        return Response.builder()
                .message("Success")
                .status(HttpStatus.OK.value())
                .data(categories)
                .build();
    }

    @PostMapping("/admin/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
    }

}