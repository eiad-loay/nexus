package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.OrderDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.service.OrderService;
import com.nexus.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<List<OrderDto>> getOrders() {
        User user = userService.getActiveUser();
        log.info("GET /api/orders - Fetching orders for user: {}", user.getEmail());

        List<OrderDto> orders = orderService.getOrders(user.getId()).stream()
                .map(orderService::mapToDto)
                .toList();
        log.debug("Found {} orders for user", orders.size());

        return Response.<List<OrderDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Orders retrieved successfully")
                .data(orders)
                .build();
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public Response<OrderDto> getOrder(@PathVariable Long orderId) throws AccessDeniedException {
        User user = userService.getActiveUser();
        log.info("GET /api/orders/{} - Fetching order for user: {}", orderId, user.getEmail());

        OrderDto order = orderService.getOrderById(orderId, user);
        log.debug("Order {} retrieved successfully", orderId);

        return Response.<OrderDto>builder()
                .status(HttpStatus.OK.value())
                .message("Order retrieved successfully")
                .data(order)
                .build();
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<OrderDto> checkout(@RequestParam Long addressId) {
        User user = userService.getActiveUser();
        log.info("POST /api/orders/checkout - Processing checkout for user: {} with addressId: {}", user.getEmail(),
                addressId);

        OrderDto orderDto = orderService.checkout(user, addressId);
        log.info("Checkout completed successfully for user: {}", user.getEmail());

        return Response.<OrderDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Order placed successfully")
                .data(orderDto)
                .build();
    }
}
