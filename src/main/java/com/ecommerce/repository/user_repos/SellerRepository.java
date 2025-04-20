package com.ecommerce.repository.user_repos;
import com.ecommerce.models.user.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {

    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Seller> findByEmailContainingIgnoreCase(String email, PageRequest pageRequest);

    boolean existsByCompanyNameIgnoreCase(String companyName);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByCompanyContact(String companyContact);

    Optional<Seller> findByCompanyName(String companyName);

}
