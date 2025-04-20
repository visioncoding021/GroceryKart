package com.ecommerce.repository.user_repos;
import com.ecommerce.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    public Optional<Role> findByAuthority(String authority);
}
