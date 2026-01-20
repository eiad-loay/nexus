package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.entity.ProductDto;
import com.nexus.ecommerce.entity.Category;
import com.nexus.ecommerce.entity.Product;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.ProductRepository;
import com.nexus.ecommerce.utils.specs.ProductSpecs;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final S3Service s3Service;

    public Page<ProductDto> getAllProductsByCriteria(Pageable pageable, String category, String name, String price) {
        log.debug("Fetching products with filters - category: {}, name: {}, price: {}", category, name, price);
        Specification<Product> specs = Specification.unrestricted();

        if (category != null) {
            category = category.toLowerCase();
            specs = specs.and(ProductSpecs.byCategory(category));
        }

        if (StringUtils.isNotEmpty(name)) {
            specs = specs.and(ProductSpecs.containsName(name));
        }

        if (StringUtils.isNotEmpty(price)) {
            String[] prices = price.split(",");

            if (prices.length != 2) {
                log.warn("Invalid price filter format: {}", price);
                throw new IllegalArgumentException(
                        "Price filter should contain exactly 2 values separated by comma (e.g., 100,500)");
            }

            BigDecimal min = new BigDecimal(
                    (prices[0].isEmpty() || Double.isNaN(Double.parseDouble(prices[0])) ? "0" : prices[0]));
            BigDecimal max = new BigDecimal(
                    (prices[1].isEmpty() || Double.isNaN(Double.parseDouble(prices[1])) ? "10000" : prices[1]));

            specs = specs.and(ProductSpecs.priceWithinRange(min, max));
            log.debug("Price filter applied: {} to {}", min, max);
        }

        Page<Product> result = productRepository.findAll(specs, pageable);
        log.debug("Found {} products matching criteria", result.getTotalElements());
        return result.map(this::mapToDto);
    }

    public Product findById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found with ID: " + id));
    }

    @Transactional
    public Long save(ProductDto productDto) {
        log.info("Creating new product: {}", productDto.getName());
        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());
        return savedProduct.getId();
    }

    @Transactional
    public void update(Long id, ProductDto productDto) {
        log.info("Updating product with ID: {}", id);

        Product found = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found with ID: " + id));

        found.setName(productDto.getName());
        found.setDescription(productDto.getDescription());
        found.setPrice(productDto.getPrice());
        found.setStock(productDto.getStock());

        if (productDto.getCategory() != null) {
            Category category = categoryService.findByName(productDto.getCategory());
            found.setCategory(category);
        }

        productRepository.save(found);
        log.info("Product {} updated successfully", id);
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product found = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found with ID: " + id)
        );
        s3Service.deleteImage(found.getImageUrl());
        productRepository.deleteById(id);
        log.info("Product {} deleted successfully", id);
    }

    @Transactional
    public void updateProductImage(Long productId, String imageUrl) {
        log.info("Updating image for product with ID: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product not found with ID: " + productId)
        );

        if (product.getImageUrl() != null) {
            s3Service.deleteImage(product.getImageUrl());
        }

        product.setImageUrl(imageUrl);
        productRepository.save(product);
    }

    public ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory().getName())
                .imageUrl(product.getImageUrl() != null
                        ? s3Service.getPresignedGetUrl(product.getImageUrl()).uploadUrl()
                        : null)
                .build();
    }

    public Product mapToEntity(ProductDto productDto) {
        Category category = categoryService.findByName(productDto.getCategory());
        return Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .category(category)
                .build();
    }
}
