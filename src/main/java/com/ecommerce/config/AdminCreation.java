package com.ecommerce.config;

import com.ecommerce.models.user.Role;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.RoleRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Scanner;

@Configuration
public class AdminCreation {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository,RoleRepository roleRepository, PasswordEncoder passwordEncoder){
        return args-> {
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
                token.setUser(admin);
                admin.setToken(token);

                admin.setIsActive(true);
                userRepository.save(admin);
                System.out.println("Admin created successfully");
            }
        };
    }
}
