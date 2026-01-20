package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.entity.OrderDto;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.Item;
import com.nexus.ecommerce.entity.Order;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.CartRepository;
import com.nexus.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final CartRepository cartRepository;

    public OrderDto getOrderById(Long orderId, User user) throws AccessDeniedException {
        log.debug("Fetching order {} for user {}", orderId, user.getEmail());

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to access order {} belonging to another user", user.getEmail(), orderId);
            throw new AccessDeniedException("You can only view your own orders");
        }

        return mapToDto(order);
    }

    public List<Order> getOrders(Long userId) {
        log.debug("Fetching all orders for userId={}", userId);
        return orderRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("No orders found"));
    }

    @Transactional
    public OrderDto checkout(Cart cart, User user) {
        log.info("Processing checkout for user: {}", user.getEmail());

        if (cart.getItems().isEmpty()) {
            log.warn("Checkout attempted with empty cart for user: {}", user.getEmail());
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setItems(cart.getItems());

        BigDecimal totalPrice = cart.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
        log.debug("Order total: {}", totalPrice);

        cart.getItems().forEach(item -> {
            item.setCart(null);
            item.setOrder(order);
        });

        orderRepository.save(order);
        cart.setItems(new java.util.HashSet<>());
        cartRepository.save(cart);

        log.info("Order {} created successfully for user: {}, total: {}", order.getId(), user.getEmail(), totalPrice);
        return mapToDto(order);
    }

    public OrderDto mapToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setItems(order.getItems().stream().map(itemService::mapToDto).collect(Collectors.toSet()));
        orderDto.setTotalPrice(order.getTotalPrice());
        return orderDto;
    }
}
