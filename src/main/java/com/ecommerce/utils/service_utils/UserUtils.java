package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.request_dto.CustomerRequestDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import com.ecommerce.models.user.Address;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.models.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public final class UserUtils {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean isAddressValid(CustomerRequestDto customerDTO) {
        if (customerDTO.getAddressLine() == null || customerDTO.getCity() == null || customerDTO.getState() == null || customerDTO.getCountry() == null || customerDTO.getZipCode() == null) {
            return false;
        }
        return  !customerDTO.getAddressLine().isEmpty() && !customerDTO.getCity().isEmpty() && !customerDTO.getState().isEmpty() && !customerDTO.getCountry().isEmpty() && !customerDTO.getZipCode().isEmpty();
    }

    public static void setPasswordEncoder(User user){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public static void setUserAddress(Address address, User user) {
        address.setUser(user);
        List<Address> listOfAddress = user.getAddress();
        if(!listOfAddress.contains(address)) {
            listOfAddress.add(address);
            user.setAddress(listOfAddress);
        }
    }

    public static boolean is24HoursExpired(long issuedAtTimestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        long twentyFourHoursMillis = 24 * 60 * 60 * 1000; // 86400000 ms in 24 hours
        return currentTimeMillis - issuedAtTimestamp >= twentyFourHoursMillis;
    }


    public static PaginatedResponseDto<List<AllCustomersResponseDto>> getCustomerPaginatedResponse(Page<Customer> customers) {
        List<Customer> customerList = customers.getContent();
        List<AllCustomersResponseDto> customerResponseDtoList = new ArrayList<>();
        for(Customer customer : customerList) {
            AllCustomersResponseDto customerResponseDto = new AllCustomersResponseDto();
            BeanUtils.copyProperties(customer, customerResponseDto);
            customerResponseDto.setFullName(customer.getFirstName()+" "+customer.getMiddleName()+" "+customer.getLastName());
            customerResponseDtoList.add(customerResponseDto);
        }
        return new PaginatedResponseDto<>(
                200,
                "All registered Customer List",
                customerResponseDtoList,
                customers.getTotalElements(),
                customers.getTotalPages(),
                customers.getSize(),
                customers.getNumber()
        );
    }

    public static PaginatedResponseDto<List<AllSellersResponseDto>> getSellerPaginatedResponse(Page<Seller> sellers) {
        List<Seller> customerList = sellers.getContent();
        List<AllSellersResponseDto> sellerResponseDtoList  = new ArrayList<>();
        for(Seller seller : customerList) {
            AllSellersResponseDto sellerResponseDto = new AllSellersResponseDto();
            BeanUtils.copyProperties(seller, sellerResponseDto);
            sellerResponseDto.setFullName(seller.getFirstName()+" "+seller.getMiddleName()+" "+seller.getLastName());
            sellerResponseDtoList.add(sellerResponseDto);
        }
        return new PaginatedResponseDto<>(
                200,
                "All registered Sellers List",
                sellerResponseDtoList,
                sellers.getTotalElements(),
                sellers.getTotalPages(),
                sellers.getSize(),
                sellers.getNumber()
        );
    }

}