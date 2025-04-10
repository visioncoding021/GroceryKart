package com.ecommerce.service.register_service;

import com.ecommerce.dto.request_dto.CustomerRequestDto;
import com.ecommerce.dto.request_dto.SellerRequestDto;
import com.ecommerce.exception.seller.SellerValidationException;
import com.ecommerce.exception.user.UserAlreadyRegistered;
import com.ecommerce.models.user.*;
import com.ecommerce.repository.user_repos.*;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.service_utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    public Customer registerCustomer(CustomerRequestDto customerRequestDTO) throws MessagingException {

        Address address;
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER").orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Customer customer = objectMapper.convertValue(customerRequestDTO, Customer.class);

        if (userRepository.existsByEmail(customerRequestDTO.getEmail())) {
            User user = userRepository.findByEmail(customerRequestDTO.getEmail()).get();
            if (!user.getIsActive()) {
                tokenService.saveActivationToken(user, "Account Activation Pending");
            }
            throw new UserAlreadyRegistered();
        }

        if(!UserUtils.isPasswordMatching(customerRequestDTO.getPassword(), customerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        customer.setRole(role);
        UserUtils.setPasswordEncoder(customer);
        customer = customerRepository.save(customer);

        if(UserUtils.isAddressValid(customerRequestDTO)){
            address = objectMapper.convertValue(customerRequestDTO,Address.class);
            UserUtils.setUserAddress(address, customer);
            addressRepository.save(address);
        }
        customer = customerRepository.save(customer);
        tokenService.saveActivationToken(customer, "Account Activation Link Sent");

        return customer;
    }

    @Transactional
    public Seller registerSeller(SellerRequestDto sellerRequestDTO) throws MessagingException {

        Address address;
        Role role = roleRepository.findByAuthority("ROLE_SELLER").orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (!UserUtils.isPasswordMatching(sellerRequestDTO.getPassword(), sellerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        List<String> errorList = new ArrayList<>();
        if(userRepository.existsByEmail(sellerRequestDTO.getEmail())) errorList.add("Email already registered");
        if(sellerRepository.existsByCompanyNameIgnoreCase(sellerRequestDTO.getCompanyName()))  errorList.add("Company name already exists");
        if(sellerRepository.existsByCompanyContact(sellerRequestDTO.getCompanyContact()))  errorList.add("Company Contact already exists");
        if(sellerRepository.existsByGstNumber(sellerRequestDTO.getGstNumber()))  errorList.add("Company Gst Number already registered");
        if(!errorList.isEmpty()) throw new SellerValidationException(errorList);

        Seller seller = objectMapper.convertValue(sellerRequestDTO, Seller.class);
        seller.setPassword(sellerRequestDTO.getPassword());
        seller.setRole(role);
        UserUtils.setPasswordEncoder(seller);
        seller = sellerRepository.save(seller);

        address = objectMapper.convertValue(sellerRequestDTO.getCompanyAddress(), Address.class);
        UserUtils.setUserAddress(address, seller);
        addressRepository.save(address);
        seller = sellerRepository.save(seller);

        Token userToken = seller.getToken();
        if (userToken == null) {
            userToken = new Token();
            userToken.setUser(seller);
            seller.setToken(userToken);
        }
        tokenRepository.save(userToken);

        emailService.sendEmail("ininsde15@gmail.com", "Account Pending Approval",
                "Your seller account has been created and is pending approval.");
        return seller;
    }

}
