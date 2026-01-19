package com.nexus.ecommerce.repository;

import com.nexus.ecommerce.entity.Order;
import com.nexus.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByUserId(Long userId);
}
