package com.novus.ecommerce.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private ProductDto product;
    private Integer quantity;
    private BigDecimal price;
}
