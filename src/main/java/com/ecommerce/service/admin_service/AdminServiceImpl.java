package com.ecommerce.service.admin_service;

import com.ecommerce.dto.request_dto.pagination_dto.UserListRequestDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Role;
import com.ecommerce.models.user.Seller;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.product_repos.ProductRepository;
import com.ecommerce.repository.user_repos.CustomerRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.service_utils.UserUtils;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public PaginatedResponseDto<List<AllCustomersResponseDto>> getAllCustomers(UserListRequestDto customerListRequestDto) {
        int pageSize = customerListRequestDto.getPageSize();
        int pageOffset = customerListRequestDto.getPageOffset();
        String emailFilter = customerListRequestDto.getEmailFilter();
        String sort = customerListRequestDto.getSort();
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        if (emailFilter != null && !emailFilter.isEmpty()) {
            Page<Customer> customerPage = customerRepository.findByEmailContainingIgnoreCase(emailFilter, pageRequest);
            return UserUtils.getCustomerPaginatedResponse(customerPage);
        }
        return UserUtils.getCustomerPaginatedResponse(customerRepository.findAll(pageRequest));
    }

    @Override
    public PaginatedResponseDto<List<AllSellersResponseDto>> getAllSellers(UserListRequestDto sellerListRequestDto) {
        int pageSize = sellerListRequestDto.getPageSize();
        int pageOffset = sellerListRequestDto.getPageOffset();
        String emailFilter = sellerListRequestDto.getEmailFilter();
        String sort = sellerListRequestDto.getSort();

        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        if (emailFilter != null && !emailFilter.isEmpty()) {
            Page<Seller> sellerPage = sellerRepository.findByEmailContainingIgnoreCase(emailFilter, pageRequest);
            return UserUtils.getSellerPaginatedResponse(sellerPage);
        }
        return UserUtils.getSellerPaginatedResponse(sellerRepository.findAll(pageRequest));
    }

    @Override
    public String activateUser(UUID userId) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Role role = user.getRole();
        String authority = role.getAuthority().replace("ROLE_", "").toLowerCase();

        if (authority.equals("ROLE_ADMIN".toLowerCase()))
            throw new UnsupportedOperationException("Admin cant be updated");
        if (user.getIsActive()) {
            throw new BadRequestException(authority + " with id " + userId + " is already active");
        }
        user.setIsActive(true);
        userRepository.save(user);
        return authority + " with id " + userId + " is now active";
    }

    @Override
    public String deactivateUser(UUID userId) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Role role = user.getRole();
        String authority = role.getAuthority().replace("ROLE_", "").toLowerCase();

        if (authority.equals("ROLE_ADMIN".toLowerCase())) return "Admin cant be updated";
        if (!user.getIsActive()) {
            throw new BadRequestException(authority + " with id " + userId + " is already active");
        }
        user.setIsActive(false);
        userRepository.save(user);
        return authority + " with id " + userId + " is now deactivated";
    }

    @Override
    public String unlockUser(UUID userId) throws BadRequestException, MessagingException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Role role = user.getRole();
        String authority = role.getAuthority();

        if (authority.equals("ROLE_ADMIN")) throw new BadRequestException("Admin cant be updated");
        if (!user.getIsLocked()) {
            throw new BadRequestException(authority + " with id " + userId + " is already unlocked");
        }
        user.setIsLocked(false);
        user.setInvalidAttemptCount(0);
        userRepository.save(user);

        emailService.sendEmail("ininsde15@gmail.com", "Your account has been unlocked", "Your account has been unlocked. You can now login to your account. Email has been sent to " + user.getEmail());
        return authority + " with id " + userId + " is now unlocked";
    }


}
