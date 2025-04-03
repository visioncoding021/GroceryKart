package com.ecommerce.service.user_service;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.models.user.*;
import com.ecommerce.repository.user_repos.*;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import com.ecommerce.utils.service_utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;


    public Customer registerCustomer(CustomerRequestDTO customerRequestDTO) throws MessagingException {

        Address address = null;
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER");

        if (customerRequestDTO.getEmail() == null || customerRequestDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (userRepository.existsByEmail(customerRequestDTO.getEmail())) {
            if (!userRepository.findByEmail(customerRequestDTO.getEmail()).isActive()) {

                emailService.sendActivationEmail("ininsde15@gmail.com", "Account Activation",
                        "Please activate your account by clicking the link below.", activationToken);
            } else {
                userRepository.delete(userRepository.findByEmail(customerRequestDTO.getEmail()));
            }
        }

        if(!UserUtils.isPasswordMatching(customerRequestDTO.getPassword(), customerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Customer customer = objectMapper.convertValue(customerRequestDTO, Customer.class);

        customer.setRole(role);
        UserUtils.setPasswordEncoder(customer);
        customer = customerRepository.save(customer);

        if(UserUtils.isAddressValid(customerRequestDTO)){
            address = objectMapper.convertValue(customerRequestDTO,Address.class);
            setUserAddress(address, customer);
            customer = customerRepository.save(customer);
        }

        String email = customer.getEmail();
        String activationToken = JwtUtil.generateToken(email);

        emailService.sendActivationEmail("ininsde15@gmail.com", "Account Activation",
                "Please activate your account by clicking the link below.", activationToken);

        return customer;
    }

    public Seller registerSeller(SellerRequestDTO sellerRequestDTO) throws MessagingException {

        Address address = null;
        Role role = roleRepository.findByAuthority("ROLE_SELLER");

        if (sellerRequestDTO.getEmail() == null || sellerRequestDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (!UserUtils.isPasswordMatching(sellerRequestDTO.getPassword(), sellerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Seller seller = objectMapper.convertValue(sellerRequestDTO, Seller.class);

        seller.setRole(role);
        UserUtils.setPasswordEncoder(seller);
        seller = sellerRepository.save(seller);


        address = objectMapper.convertValue(sellerRequestDTO, Address.class);
        setUserAddress(address, seller);
        seller = sellerRepository.save(seller);


        emailService.sendEmail("ininsde15@gmail.com", "Account Pending Approval",
                "Your seller account has been created and is pending approval.");
        return seller;
    }


    /**
     * Activates the user account using the provided token.
     *
     * @param token The activation token.
     * @return true if the account is activated successfully, false otherwise.
     * @throws MessagingException if there is an error sending the email.
     */
    public boolean activateUser(String token) throws MessagingException {
        System.out.println("Token1: " + token);
        String email = JwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email);
        System.out.println("Token2: " + token);
        if (user == null) {
            emailService.sendEmail("ininsde15@gmail.com", "Account Activation Failed",
                    "You are not registered user. Please register again.");
            return false;
        }

        if (user.isActive()) {
            emailService.sendEmail("ininsde15@gmail.com", "Account Already Activated",
                    "Your account is already activated. You can login.");
            return true;
        }

        System.out.println("Token: " + token);

        if (!JwtUtil.isTokenValid(token)) {
            String activationToken = JwtUtil.generateToken(email);
            emailService.sendActivationEmail("ininsde15@gmail.com", "Account Activation Failed",
                    "Your account activation link is either expired. Please register again.",activationToken);
            return false;
        }

        user.setActive(true);
        userRepository.save(user);

        emailService.sendEmail("ininsde15@gmail.com", "Account Activated",
                "Your account has been activated successfully. Now you can login.");

        return true;
    }

    private void setUserAddress(Address address, User user) {
        address.setUser(user);
        List<Address> listOfAddress = user.getAddress();
        if(!listOfAddress.contains(address)) {
            listOfAddress.add(address);
            user.setAddress(listOfAddress);
        }
        addressRepository.save(address);
    }
}
