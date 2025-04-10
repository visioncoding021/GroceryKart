package com.ecommerce.service.profile_service;

import com.ecommerce.dto.response_dto.user_dto.AddressResponseDto;
import com.ecommerce.dto.response_dto.user_dto.CustomerProfileResponseDto;
import com.ecommerce.dto.response_dto.user_dto.SellerProfileResponseDto;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Address;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.repository.user_repos.AddressRepository;
import com.ecommerce.repository.user_repos.CustomerRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private AddressRepository addressRepository;

    public CustomerProfileResponseDto getCustomerProfile(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));
        CustomerProfileResponseDto customerProfileResponseDto = new CustomerProfileResponseDto();
        BeanUtils.copyProperties(customer, customerProfileResponseDto);
        customerProfileResponseDto.setImageUrl(""+customer.getId());
        customerProfileResponseDto.setActive(customer.getIsActive());
        return customerProfileResponseDto;
    }

    public SellerProfileResponseDto getSellerProfile(String email) {
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Seller not found"));
        SellerProfileResponseDto sellerProfileResponseDto = new SellerProfileResponseDto();
        BeanUtils.copyProperties(seller, sellerProfileResponseDto);
        sellerProfileResponseDto.setImageUrl(""+seller.getId());
        sellerProfileResponseDto.setActive(seller.getIsActive());

        List<Address> sellerAddress = addressRepository.findByUserId(seller.getId());
        if(!sellerAddress.isEmpty()){
            sellerProfileResponseDto.setAddress(new AddressResponseDto());
            BeanUtils.copyProperties(sellerAddress.get(0), sellerProfileResponseDto.getAddress());
        }
        return sellerProfileResponseDto;
    }
}
