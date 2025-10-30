package com.project.supplychain.repositories;

import com.project.supplychain.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPassword(String password);

    User getByEmail(String email);
}
