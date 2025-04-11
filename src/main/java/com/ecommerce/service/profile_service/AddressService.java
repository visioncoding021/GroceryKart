package com.ecommerce.service.profile_service;

import com.ecommerce.dto.request_dto.AddressRequestDto;
import com.ecommerce.dto.response_dto.user_dto.AddressResponseDto;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.*;
import com.ecommerce.repository.user_repos.AddressRepository;
import com.ecommerce.repository.user_repos.CustomerRepository;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public String updateAddress(UUID addressId, AddressRequestDto addressRequestDto, HttpServletResponse response) throws BadRequestException, MessagingException {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new BadRequestException("Address not found"));
        BeanUtils.copyProperties(addressRequestDto, address);
        addressRepository.save(address);

        User user = address.getUser();
        String role = user.getRole().getAuthority();
        if(role.equals("ROLE_SELLER")) {
            Token token = user.getToken();

            token.setAccess(null);
            token.setRefresh(null);
            user.setIsLocked(true);

            userRepository.save(user);
            tokenRepository.save(token);
            emailService.sendEmail(
                    "ininsde15@gmail.com",
                    "Address Updated",
                    "Your address has been updated successfully. Please wait till admin approves your address change. " +
                            "You will be notified via email once the approval is done.");

            ResponseCookie deleteRefreshCookie = ResponseCookie.from("refresh", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());

            return "Seller address updated and logout successfully. Now admin will approve your address change";
        }
        return "Customer address updated successfully";
    }

    public List<AddressResponseDto> getAllAddresses(UUID customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        List<AddressResponseDto> listOfAddress = new ArrayList<>();
        for (Address address : customer.getAddress()) {
            AddressResponseDto addressResponseDto = new AddressResponseDto();
            BeanUtils.copyProperties(address, addressResponseDto);
            listOfAddress.add(addressResponseDto);
        }
        return listOfAddress;
    }
}
