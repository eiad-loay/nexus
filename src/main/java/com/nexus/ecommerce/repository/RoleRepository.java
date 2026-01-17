package com.nexus.ecommerce.repository;

import com.nexus.ecommerce.entity.ERole;
import com.nexus.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(ERole roleName);
}
