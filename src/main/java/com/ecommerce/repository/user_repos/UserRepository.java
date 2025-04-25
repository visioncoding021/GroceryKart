package com.ecommerce.repository.user_repos;

import com.ecommerce.models.user.Role;
import com.ecommerce.models.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);
    public boolean existsByEmail(String email);
    public boolean existsByRole(Role role);
    public Optional<User> findById(UUID id);

    List<User> findAllByIsActiveFalse();

    @Modifying
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void activateUser(@Param("id") UUID id);
}
