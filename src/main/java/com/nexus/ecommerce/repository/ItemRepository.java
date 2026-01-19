package com.nexus.ecommerce.repository;

import com.nexus.ecommerce.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
