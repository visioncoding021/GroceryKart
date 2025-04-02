package com.ecommerce.repository.user_repos;
import com.ecommerce.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Role findByAuthority(String authority);
}
