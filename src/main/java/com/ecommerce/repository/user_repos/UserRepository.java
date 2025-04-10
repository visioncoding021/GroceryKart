package com.ecommerce.repository.user_repos;

import com.ecommerce.models.user.Role;
import com.ecommerce.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
    public boolean existsByEmail(String email);
    public boolean existsByRole(Role role);
}
