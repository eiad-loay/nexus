package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.OrderDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.service.CartService;
import com.nexus.ecommerce.service.OrderService;
import com.nexus.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<List<OrderDto>> getOrders() {
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByEmail(userEmail);

        List<OrderDto> orders = orderService.getOrders(user.getId()).stream().map(orderService::mapToDto).toList();

        return Response.<List<OrderDto>>builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(orders)
                .build();

    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<OrderDto> getOrder(@PathVariable Long orderId) {
        OrderDto order = orderService.getOrderById(orderId);

        return Response.<OrderDto>builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(order)
                .build();
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.OK)
    public Response<OrderDto> checkout() {
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByEmail(userEmail);
        Cart cart = cartService.getCart(user.getId());

        OrderDto orderDto = orderService.checkout(cart, user);

        return Response.<OrderDto>builder()
                .status(HttpStatus.OK.value())
                .message("successful order checkout")
                .data(orderDto)
                .build();
    }
}
