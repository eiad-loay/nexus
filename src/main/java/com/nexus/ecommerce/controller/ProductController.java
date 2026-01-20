package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.ProductDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.Product;
import com.nexus.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<?> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false, name = "category") String category,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "price") String price) {
        log.info("GET /api/products - Fetching products with filters: category={}, name={}, price={}", category, name,
                price);
        Page<ProductDto> products = productService.getAllProductsByCriteria(pageable, category, name, price);
        log.debug("Found {} products", products.getTotalElements());

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Products retrieved successfully")
                .data(products.get().collect(Collectors.toSet()))
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(products.getTotalPages())
                .totalData(products.getTotalElements())
                .build();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> findById(@PathVariable Long productId) {
        log.info("GET /api/products/{} - Fetching product by ID", productId);
        Product product = productService.findById(productId);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Product retrieved successfully")
                .data(productService.mapToDto(product))
                .build();
    }

    @PostMapping("/admin/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<?> save(@RequestBody @Valid ProductDto product) {
        log.info("POST /api/products/admin/add - Creating new product: {}", product.getName());
        Long id = productService.save(product);
        log.info("Product created successfully with ID: {}", id);

        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .data(id)
                .build();
    }

    @PutMapping("/admin/update/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> update(@PathVariable Long productId, @RequestBody @Valid ProductDto product) {
        log.info("PUT /api/products/admin/update/{} - Updating product", productId);
        productService.update(productId, product);
        log.info("Product with ID {} updated successfully", productId);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Product updated successfully")
                .build();
    }

    @DeleteMapping("/admin/delete/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> delete(@PathVariable Long productId) {
        log.info("DELETE /api/products/admin/delete/{} - Deleting product", productId);
        productService.deleteById(productId);
        log.info("Product with ID {} deleted successfully", productId);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Product deleted successfully")
                .build();
    }
}