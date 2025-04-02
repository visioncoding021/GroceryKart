package com.ecommerce.service.user_service;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.models.user.Address;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Role;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.*;
import com.ecommerce.service.email_service.EmailService;
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

        if(!UserUtils.isPasswordMatching(customerRequestDTO.getPassword(), customerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Customer customer = objectMapper.convertValue(customerRequestDTO, Customer.class);
        customer.setRole(role);
        customer = customerRepository.save(customer);
        if(UserUtils.isAddressValid(customerRequestDTO)){
            address = objectMapper.convertValue(customerRequestDTO,Address.class);
            setUserAddress(address, customer);
            customer = customerRepository.save(customer);
        }
        emailService.sendEmail("ininsde15@gmail.com","Registered Succesfully","You are registered succesfully as a customer.");
        return customer;
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
