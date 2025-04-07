package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.models.user.Address;
import com.ecommerce.models.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public final class UserUtils {


    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean isAddressValid(CustomerRequestDTO customerDTO) {
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

}