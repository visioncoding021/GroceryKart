package com.ecommerce.repository.user_repos;

import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByUser(User user);

}
