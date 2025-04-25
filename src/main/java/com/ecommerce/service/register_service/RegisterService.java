package com.ecommerce.service.register_service;

import com.ecommerce.dto.request_dto.user_dto.CustomerRequestDto;
import com.ecommerce.dto.request_dto.user_dto.SellerRequestDto;
import com.ecommerce.exception.seller.SellerValidationException;
import com.ecommerce.exception.user.RoleNotFoundException;
import com.ecommerce.exception.user.UserAlreadyRegistered;
import com.ecommerce.models.user.*;
import com.ecommerce.repository.user_repos.*;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.service_utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer registerCustomer(CustomerRequestDto customerRequestDTO, Locale locale) throws MessagingException {

        Address address;
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER").orElseThrow(() -> new RoleNotFoundException(
                messageSource.getMessage("error.roleNotFound", null, locale)
        ));
        Customer customer = objectMapper.convertValue(customerRequestDTO, Customer.class);

        if (userRepository.existsByEmail(customerRequestDTO.getEmail())) {
            User user = userRepository.findByEmail(customerRequestDTO.getEmail()).get();
            if (!user.getIsActive()) {
                tokenService.saveActivationToken(user,  messageSource.getMessage("email.resendActivationToken.subject",null, locale));
            }
            throw new UserAlreadyRegistered(messageSource.getMessage("response.activation.alreadyActivated",null, locale));
        } else if (customerRepository.existsByContact(customerRequestDTO.getContact())) {
            throw new UserAlreadyRegistered(messageSource.getMessage("error.phone.exists", null, locale));
        }

        if(!UserUtils.isPasswordMatching(customerRequestDTO.getPassword(), customerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException(messageSource.getMessage("error.password.mismatch", null, locale));
        }

        customer.setRole(role);
        customer.setPassword(passwordEncoder.encode(customerRequestDTO.getPassword()));
        customer = customerRepository.save(customer);

        if(UserUtils.isAddressValid(customerRequestDTO)){
            address = objectMapper.convertValue(customerRequestDTO,Address.class);
            UserUtils.setUserAddress(address, customer);
            addressRepository.save(address);
        }
        customer = customerRepository.save(customer);
        tokenService.saveActivationToken(customer, messageSource.getMessage("response.activation.resend", null, locale));

        return customer;
    }

    @Transactional
    public Seller registerSeller(SellerRequestDto sellerRequestDTO , Locale locale) throws MessagingException {

        Address address;
        Role role = roleRepository.findByAuthority("ROLE_SELLER").orElseThrow(() -> new RoleNotFoundException(messageSource.getMessage("error.roleNotFound", null, locale)));

        if (!UserUtils.isPasswordMatching(sellerRequestDTO.getPassword(), sellerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException(messageSource.getMessage("error.password.mismatch", null, locale));
        }

        List<String> errorList = new ArrayList<>();
        if (userRepository.existsByEmail(sellerRequestDTO.getEmail()))
            errorList.add(messageSource.getMessage("error.email.exists", null, locale));

        if (sellerRepository.existsByCompanyNameIgnoreCase(sellerRequestDTO.getCompanyName()))
            errorList.add(messageSource.getMessage("error.companyName.exists", null, locale));

        if (sellerRepository.existsByCompanyContact(sellerRequestDTO.getCompanyContact()))
            errorList.add(messageSource.getMessage("error.companyContact.exists", null, locale));

        if (sellerRepository.existsByGstNumber(sellerRequestDTO.getGstNumber()))
            errorList.add(messageSource.getMessage("error.gst.exists", null, locale));

        if (!errorList.isEmpty())
            throw new SellerValidationException(errorList);

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

        emailService.sendEmail("ininsde15@gmail.com", messageSource.getMessage("email.sellerPendingApproval.subject", null, locale),
                messageSource.getMessage("email.sellerPendingApproval.body", null, locale));
        return seller;
    }

    @Scheduled(fixedRate = 86400000)
    public void sendEmailToAdminForActivationOfUsers(){
        List<String> pendingUsers = userRepository.findAllByIsActiveFalse().stream().map(User::getEmail).toList();
        System.out.println(pendingUsers.toString());
        try {
            emailService.sendEmail(
                    "ininsde15@gmail.com",
                    "Daily Active Users Report",
                    "This is your scheduled report on active users." + "\nPending Users: " + pendingUsers.toString()
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
