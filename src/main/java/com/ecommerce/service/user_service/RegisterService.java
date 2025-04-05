package com.ecommerce.service.user_service;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.exception.user.UserAlreadyRegistered;
import com.ecommerce.models.user.*;
import com.ecommerce.repository.user_repos.*;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import com.ecommerce.utils.service_utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
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

        Address address;
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER");

        if (userRepository.existsByEmail(customerRequestDTO.getEmail())) {
            User user = userRepository.findByEmail(customerRequestDTO.getEmail()).get();
            if (!user.isActive()) {
                String activationToken = JwtUtil.generateToken(user,"activationToken",10800000);
                emailService.sendActivationEmail("ininsde15@gmail.com", "Already registeredAccount Activation",
                        "Please activate your account by clicking the link below.", activationToken);
            }
            throw new UserAlreadyRegistered();
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
            UserUtils.setUserAddress(address, customer);
            addressRepository.save(address);
            customer = customerRepository.save(customer);
        }

        String activationToken = JwtUtil.generateToken(customer,"activationToken",10800000);

        emailService.sendActivationEmail("ininsde15@gmail.com", "Account Activation",
                "Please activate your account by clicking the link below.", activationToken);

        return customer;
    }


    public Seller registerSeller(SellerRequestDTO sellerRequestDTO) throws MessagingException {

        Address address;
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
        UserUtils.setUserAddress(address, seller);
        addressRepository.save(address);
        seller = sellerRepository.save(seller);


        emailService.sendEmail("ininsde15@gmail.com", "Account Pending Approval",
                "Your seller account has been created and is pending approval.");
        return seller;
    }

}
