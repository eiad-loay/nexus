package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.entity.CartDto;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.Item;
import com.nexus.ecommerce.entity.Product;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemService itemService;

    public CartDto mapToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setItems(cart.getItems().stream().map(itemService::mapToDto).collect(Collectors.toSet()));
        return cartDto;
    }

    public Cart getCart(Long userId) {
        return  cartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Cart not found")
        );
    }

    @Transactional
    public void addProductToCart(Cart cart, Product product, Integer quantity) {

        if (containsInCart(cart, product)) {
            cart.getItems().stream().filter(item -> item.getProduct().getId().equals(product.getId())).findFirst().ifPresent(item -> {
                item.setQuantity(item.getQuantity() + quantity);
                item.setPrice(item.getPrice().add(product.getPrice().multiply(BigDecimal.valueOf(quantity))));
            });
        } else {
            Item item = new Item();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
    }

    @Transactional
    public void removeProductFromCart(Cart cart, Product product) {
        cart.getItems().stream().filter(item -> item.getProduct().equals(product)).findFirst().ifPresent(item -> {
            cart.getItems().remove(item);
            cartRepository.save(cart);
        });
    }

    private boolean containsInCart(Cart cart, Product product) {
        return cart.getItems().stream().anyMatch(item -> item.getProduct().equals(product));
    }
}
