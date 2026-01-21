package com.nexus.ecommerce.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private Set<ItemDto> items;
    private BigDecimal totalPrice;
    private String status;
    private java.time.LocalDateTime createdAt;

}
