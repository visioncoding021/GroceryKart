package com.ecommerce.repository.user_repos;

import com.ecommerce.models.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByContact(String contact);
}
