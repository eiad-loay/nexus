package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.CartDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.Product;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.service.CartService;
import com.nexus.ecommerce.service.ProductService;
import com.nexus.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<CartDto> getCart() {
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByEmail(userEmail);

        Cart cart = cartService.getCart(user.getId());
        CartDto cartDto = cartService.mapToDto(cart);

        return Response.<CartDto>builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(cartDto)
                .build();
    }

    @PostMapping("/add/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<?> addProductToCart(@PathVariable Long productId, @RequestParam Integer quantity) {
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByEmail(userEmail);
        Cart cart = cartService.getCart(user.getId());

        Product product = productService.findById(productId);

        cartService.addProductToCart(cart, product, quantity);

        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("success")
                .build();
    }

    @DeleteMapping("/delete/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> deleteProductFromCart(@PathVariable Long productId) {
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByEmail(userEmail);
        Cart cart = cartService.getCart(user.getId());

        Product product = productService.findById(productId);

        cartService.removeProductFromCart(cart, product);

        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("success")
                .build();
    }

}