package com.novus.ecommerce.service;

import com.novus.ecommerce.dto.entity.ItemDto;
import com.novus.ecommerce.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ProductService productService;

    public ItemDto mapToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setProduct(productService.mapToDto(item.getProduct()));
        itemDto.setQuantity(item.getQuantity());
        itemDto.setPrice(item.getPrice());
        return itemDto;
    }
}
