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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public Page<ProductDto> getAllProductsByCriteria(Map<String, String> searchCriteria, Pageable pageable, String category) {
        Specification<Product> specs = Specification.unrestricted();

        if (category != null) {
            category = category.toLowerCase();
            specs = specs.and(ProductSpecs.byCategory(category));
        }

        if (searchCriteria != null) {
            if (searchCriteria.containsKey("name") && StringUtils.isNotEmpty(searchCriteria.get("name"))) {
                specs = specs.and(ProductSpecs.containsName(searchCriteria.get("name")));
            }

            if (searchCriteria.containsKey("price") && StringUtils.isNotEmpty(searchCriteria.get("price"))) {
                String[] prices = searchCriteria.get("price").split(",");

                if (prices.length != 2) {
                    throw new IllegalArgumentException("Prices should contain exactly 2 values separated by comma");
                }

                BigDecimal min =  new BigDecimal((prices[0].isEmpty() || Double.isNaN(Double.parseDouble(prices[0])) ? "0" : prices[0]));
                BigDecimal max =  new BigDecimal((prices[1].isEmpty() || Double.isNaN(Double.parseDouble(prices[1])) ? "10000" : prices[1]));

                specs = specs.and(ProductSpecs.priceWithinRange(min, max));
            }
        }

        return productRepository.findAll(specs, pageable).map(this::mapToDto);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + id + " not found")
        );
    }

    @Transactional
    public Long save(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Transactional
    public void update(Long id, ProductDto product) {
        Product found = findById(id);
        found.setName(product.getName());
        found.setDescription(product.getDescription());
        found.setPrice(product.getPrice());
        found.setStock(product.getStock());
        productRepository.save(found);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public ProductDto mapToDto(Product product) {

        return ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory().getName())
                .imageUrl(product.getImageUrl())
                .build();

    }

    public Product mapToEntity(ProductDto productDto) {
        Category category = categoryService.findByName(productDto.getCategory());
        return Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .imageUrl(productDto.getImageUrl())
                .category(category)
                .build();
    }
}
