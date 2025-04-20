package com.ecommerce.repository.user_repos;

import com.ecommerce.models.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    public Optional<Address> findById(UUID id);
    public List<Address> findByUserId(UUID userId);
}
