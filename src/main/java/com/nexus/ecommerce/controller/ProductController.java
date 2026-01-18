package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.ProductDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<?> getAllProducts(@RequestBody(required = false) Map<String, String> searchCriteria, Pageable pageable, @RequestParam(required = false, name = "category") String category) {
        Page<ProductDto> products = productService.getAllProductsByCriteria(searchCriteria, pageable, category);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Successful")
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
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(productService.findById(productId))
                .build();
    }

    @PostMapping("/admin/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<?> save(@RequestBody ProductDto product) {
        Long id = productService.save(product);
        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("Added product with id:" + id)
                .build();
    }

    @PutMapping("/admin/update/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> update(@PathVariable Long productId, @RequestBody ProductDto product) {
        productService.update(productId, product);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("updated")
                .build();
    }

    @DeleteMapping("/admin/delete/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> delete(@PathVariable Long productId) {
        productService.deleteById(productId);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("deleted")
                .build();
    }
}