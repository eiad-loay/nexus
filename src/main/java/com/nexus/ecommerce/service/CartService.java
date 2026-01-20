package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.entity.CartDto;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.Item;
import com.nexus.ecommerce.entity.Product;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ItemService itemService;

    public CartDto mapToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setItems(cart.getItems().stream().map(itemService::mapToDto).collect(Collectors.toSet()));
        return cartDto;
    }

    public Cart getCart(Long userId) {
        log.debug("Fetching cart for userId={}", userId);
        return cartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Cart not found for user"));
    }

    @Transactional
    public void addProductToCart(Cart cart, Product product, Integer quantity) {
        log.info("Adding product {} to cart {}, quantity: {}", product.getId(), cart.getId(), quantity);

        if (containsInCart(cart, product)) {
            log.debug("Product already in cart, updating quantity");
            cart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .ifPresent(item -> {
                        item.setQuantity(item.getQuantity() + quantity);
                        item.setPrice(item.getPrice().add(product.getPrice().multiply(BigDecimal.valueOf(quantity))));
                        log.debug("Updated item quantity to {}", item.getQuantity());
                    });
        } else {
            log.debug("Adding new product to cart");
            Item item = new Item();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            item.setCart(cart);
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
        log.info("Product {} added to cart successfully", product.getId());
    }

    @Transactional
    public void removeProductFromCart(Cart cart, Product product) {
        log.info("Removing product {} from cart {}", product.getId(), cart.getId());

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresent(item -> {
                    cart.getItems().remove(item);
                    log.debug("Product removed from cart");
                });

        cartRepository.save(cart);
        log.info("Product {} removed from cart successfully", product.getId());
    }

    private boolean containsInCart(Cart cart, Product product) {
        return cart.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.setItems(new HashSet<>());
        cartRepository.save(cart);
    }
}
