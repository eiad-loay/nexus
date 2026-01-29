package com.novus.ecommerce.service;

import com.novus.ecommerce.dto.entity.OrderDto;
import com.novus.ecommerce.dto.entity.ProductDto;
import com.novus.ecommerce.entity.*;
import com.novus.ecommerce.exception.custom.EntityNotFoundException;
import com.novus.ecommerce.repository.CartRepository;
import com.novus.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final CartRepository cartRepository;
    private final com.novus.ecommerce.repository.AddressRepository addressRepository;
    private final ProductService productService;

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
        return orderRepository.findByUserId(userId).orElse(List.of());
    }

    @Transactional
    public OrderDto checkout(User user, Long addressId) {
        log.info("Processing checkout for user: {}", user.getEmail());

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("Cart not found for user: " + user.getEmail()));

        if (cart.getItems().isEmpty()) {
            log.warn("Checkout attempted with empty cart for user: {}", user.getEmail());
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Invalid address for user");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);

        BigDecimal totalPrice = cart.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
        order.setStatus("Pending");
        log.debug("Order total: {}", totalPrice);

        Set<Item> orderItems = new java.util.HashSet<>();
        cart.getItems().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Product " + product.getName() + " quantity exceeds stock");
            }
            productService.update(product.getId(),
                    ProductDto.builder()
                            .description(product.getDescription())
                            .name(product.getName())
                            .price(product.getPrice())
                            .stock(product.getStock() - cartItem.getQuantity())
                            .build());
            Item orderItem = Item.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .order(order)
                    .build();
            orderItems.add(orderItem);
        });

        order.setItems(orderItems);

        orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Order {} created successfully for user: {}, total: {}", order.getId(), user.getEmail(), totalPrice);
        return mapToDto(order);
    }

    public OrderDto mapToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setItems(order.getItems().stream().map(itemService::mapToDto).collect(Collectors.toSet()));
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setCreatedAt(order.getCreatedAt());
        return orderDto;
    }
}
