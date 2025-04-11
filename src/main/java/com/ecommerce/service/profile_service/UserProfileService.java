package com.ecommerce.service.profile_service;

import com.ecommerce.dto.response_dto.user_dto.AddressResponseDto;
import com.ecommerce.dto.response_dto.user_dto.CustomerProfileResponseDto;
import com.ecommerce.dto.response_dto.user_dto.ImageResponseDto;
import com.ecommerce.dto.response_dto.user_dto.SellerProfileResponseDto;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Address;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.repository.user_repos.AddressRepository;
import com.ecommerce.repository.user_repos.CustomerRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import com.ecommerce.service.image_service.ImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public class UserProfileService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ImageService imageService;

    @Value("${image.path}")
    private String imagePath;

    public CustomerProfileResponseDto getCustomerProfile(String email) throws FileNotFoundException {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));
        CustomerProfileResponseDto customerProfileResponseDto = new CustomerProfileResponseDto();
        BeanUtils.copyProperties(customer, customerProfileResponseDto);
        try{
            FileInputStream fileInputStream = imageService.getResource(imagePath+"/users", customer.getId());
            ImageResponseDto imageResponseDto = ProfileHelper.getImageUrl(fileInputStream, customer.getId().toString());
            customerProfileResponseDto.setImageUrl(imageResponseDto);
        }catch (FileNotFoundException e){
            customerProfileResponseDto.setImageUrl(null);
        }
        customerProfileResponseDto.setActive(customer.getIsActive());
        return customerProfileResponseDto;
    }

    public SellerProfileResponseDto getSellerProfile(String email) throws FileNotFoundException {
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Seller not found"));
        SellerProfileResponseDto sellerProfileResponseDto = new SellerProfileResponseDto();
        BeanUtils.copyProperties(seller, sellerProfileResponseDto);
        try {
            FileInputStream fileInputStream = imageService.getResource(imagePath + "/users", seller.getId());
            ImageResponseDto imageResponseDto = ProfileHelper.getImageUrl(fileInputStream, seller.getId().toString());
            sellerProfileResponseDto.setImageUrl(imageResponseDto);
        }catch (FileNotFoundException e){
            sellerProfileResponseDto.setImageUrl(null);
        }
        sellerProfileResponseDto.setActive(seller.getIsActive());

        List<Address> sellerAddress = addressRepository.findByUserId(seller.getId());
        if(!sellerAddress.isEmpty()){
            sellerProfileResponseDto.setAddress(new AddressResponseDto());
            BeanUtils.copyProperties(sellerAddress.get(0), sellerProfileResponseDto.getAddress());
        }
        return sellerProfileResponseDto;
    }
}
