package com.ecommerce.service.admin_service;

import com.ecommerce.dto.request_dto.pagination_dto.UserListRequestDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;


public interface AdminService {

    public PaginatedResponseDto<List<AllCustomersResponseDto>> getAllCustomers(UserListRequestDto customerListRequestDto);

    public PaginatedResponseDto<List<AllSellersResponseDto>> getAllSellers(UserListRequestDto sellerListRequestDto);

    public String activateUser(UUID userId) throws BadRequestException ;

    public String deactivateUser(UUID userId) throws BadRequestException;

    public String unlockUser(UUID userId) throws BadRequestException, MessagingException;

}
