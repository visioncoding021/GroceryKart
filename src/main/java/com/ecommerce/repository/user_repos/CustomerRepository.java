package com.ecommerce.repository.user_repos;

import com.testpurpose.model.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
