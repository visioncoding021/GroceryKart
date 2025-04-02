package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;

public final class UserUtils {
    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean isAddressValid(CustomerRequestDTO customerDTO) {
        return  !customerDTO.getAddressLine().isEmpty() && !customerDTO.getCity().isEmpty() && !customerDTO.getState().isEmpty() && !customerDTO.getCountry().isEmpty() && !customerDTO.getZipCode().isEmpty();
    }


}