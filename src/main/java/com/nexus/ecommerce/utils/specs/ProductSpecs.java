package com.nexus.ecommerce.utils.specs;

import com.nexus.ecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecs {
    public static Specification<Product> containsName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name").as(String.class)), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> priceWithinRange(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price").as(BigDecimal.class), min, max);
    }
}