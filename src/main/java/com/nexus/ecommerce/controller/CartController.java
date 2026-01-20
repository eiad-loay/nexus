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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<CartDto> getCart() {
        User user = userService.getActiveUser();
        log.info("GET /api/cart - Fetching cart for user: {}", user.getEmail());

        Cart cart = cartService.getCart(user.getId());
        CartDto cartDto = cartService.mapToDto(cart);
        log.debug("Cart retrieved with {} items", cart.getItems().size());

        return Response.<CartDto>builder()
                .status(HttpStatus.OK.value())
                .message("Cart retrieved successfully")
                .data(cartDto)
                .build();
    }

    @PostMapping("/add/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<?> addProductToCart(@PathVariable Long productId, @RequestParam Integer quantity) {
        User user = userService.getActiveUser();
        log.info("POST /api/cart/add/{} - Adding product to cart for user: {}, quantity: {}", productId,
                user.getEmail(), quantity);

        Cart cart = cartService.getCart(user.getId());
        Product product = productService.findById(productId);
        cartService.addProductToCart(cart, product, quantity);
        log.info("Product {} added to cart successfully", productId);

        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("Product added to cart successfully")
                .build();
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> removeProductFromCart(@PathVariable Long productId) {
        User user = userService.getActiveUser();
        log.info("DELETE /api/cart/remove/{} - Removing product from cart for user: {}", productId, user.getEmail());

        Cart cart = cartService.getCart(user.getId());
        Product product = productService.findById(productId);
        cartService.removeProductFromCart(cart, product);
        log.info("Product {} removed from cart successfully", productId);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Product removed from cart successfully")
                .build();
    }

    @PostMapping("/clear")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> clearCart() {
        User user = userService.getActiveUser();
        Cart cart = cartService.getCart(user.getId());
        cartService.clearCart(cart);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Cart cleared successfully")
                .build();
    }
}