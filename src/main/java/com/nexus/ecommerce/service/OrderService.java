package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.entity.OrderDto;
import com.nexus.ecommerce.entity.Cart;
import com.nexus.ecommerce.entity.Item;
import com.nexus.ecommerce.entity.Order;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.CartRepository;
import com.nexus.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final CartRepository cartRepository;

    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order with id " + orderId + " not found")
        );
        return mapToDto(order);
    }

    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("No orders found")
        );
    }

    public OrderDto checkout(Cart cart, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setItems(cart.getItems());

        BigDecimal totalPrice = cart.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);

        cart.getItems().forEach(item -> {
            item.setCart(null);
            item.setOrder(order);
        });
        cart.getItems().clear();
        cartRepository.save(cart);
        orderRepository.save(order);
        return mapToDto(order);
    }

    public OrderDto mapToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setItems(order.getItems().stream().map(itemService::mapToDto).collect(Collectors.toSet()));
        orderDto.setTotalPrice(order.getTotalPrice());
        return orderDto;
    }
}
