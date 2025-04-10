package com.ecommerce.service.admin_service;

import com.ecommerce.dto.request_dto.UserListRequestDto;
import com.ecommerce.dto.response_dto.CustomerResponseDto;
import com.ecommerce.dto.response_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.SellerResponseDto;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.repository.user_repos.CustomerRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import com.ecommerce.utils.service_utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserUtils userUtils;

    public PaginatedResponseDto<List<CustomerResponseDto>> getAllCustomers(UserListRequestDto customerListRequestDto) {
        int pageSize = customerListRequestDto.getPageSize();
        int pageOffset = customerListRequestDto.getPageOffset();
        String emailFilter = customerListRequestDto.getEmailFilter();
        String sort = customerListRequestDto.getSort();

        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        if(emailFilter != null && !emailFilter.isEmpty()) {
            Page<Customer> customerPage = customerRepository.findByEmailContainingIgnoreCase(emailFilter, pageRequest);
            return UserUtils.getCustomerPaginatedResponse(customerPage);
        }

        return UserUtils.getCustomerPaginatedResponse(customerRepository.findAll(pageRequest));
    }

    public PaginatedResponseDto<List<SellerResponseDto>> getAllSellers(UserListRequestDto sellerListRequestDto) {
        int pageSize = sellerListRequestDto.getPageSize();
        int pageOffset = sellerListRequestDto.getPageOffset();
        String emailFilter = sellerListRequestDto.getEmailFilter();
        String sort = sellerListRequestDto.getSort();

        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        if(emailFilter != null && !emailFilter.isEmpty()) {
            Page<Seller> sellerPage = sellerRepository.findByEmailContainingIgnoreCase(emailFilter, pageRequest);
            return UserUtils.getSellerPaginatedResponse(sellerPage);
        }
        return UserUtils.getSellerPaginatedResponse(sellerRepository.findAll(pageRequest));
    }

}
