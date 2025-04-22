package com.ecommerce.config;

import com.ecommerce.models.user.Role;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.RoleRepository;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Scanner;

@Configuration
public class AdminAndRoleCreation {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository) {
        return args-> {

            if(roleRepository.findAll().isEmpty()) {
                Role roleAdmin = new Role();
                roleAdmin.setAuthority("ROLE_ADMIN");

                Role roleCustomer = new Role();
                roleCustomer.setAuthority("ROLE_CUSTOMER");

                Role roleSeller = new Role();
                roleSeller.setAuthority("ROLE_SELLER");

                roleRepository.save(roleAdmin);
                roleRepository.save(roleCustomer);
                roleRepository.save(roleSeller);
            }

            Role role = roleRepository.findByAuthority("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Role not found"));
            if (!userRepository.existsByRole(role)) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the admin email:");
                String email = scanner.nextLine();
                System.out.println("Enter the admin password:");
                String password = scanner.nextLine();

                User admin = new User();

                admin.setFirstName("Himanshi");
                admin.setLastName("Sharma");

                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));

                admin.setRole(role);

                Token token = new Token();
                tokenRepository.save(token);

                token.setUser(admin);
                admin.setToken(token);

                admin.setIsActive(true);
                userRepository.save(admin);

                System.out.println("Admin created successfully");
            }
        };
    }
}
