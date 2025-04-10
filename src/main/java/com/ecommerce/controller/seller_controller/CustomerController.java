package com.ecommerce.controller.seller_controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @PostMapping("/test")
    public String test() {
        return "Customer controller is working";
    }
}
