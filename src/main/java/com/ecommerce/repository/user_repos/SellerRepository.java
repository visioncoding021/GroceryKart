package com.ecommerce.repository.user_repos;
import com.ecommerce.models.user.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

}
