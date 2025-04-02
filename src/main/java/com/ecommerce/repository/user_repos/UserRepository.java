package com.ecommerce.repository.user_repos;

import com.ecommerce.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}
