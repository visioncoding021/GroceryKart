package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.models.user.User;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class UserUtils {


    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean isAddressValid(CustomerRequestDTO customerDTO) {
        return  !customerDTO.getAddressLine().isEmpty() && !customerDTO.getCity().isEmpty() && !customerDTO.getState().isEmpty() && !customerDTO.getCountry().isEmpty() && !customerDTO.getZipCode().isEmpty();
    }

    public static void setPasswordEncoder(User user){
        if(user.getPassword() != null && !user.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

}