package com.ecommerce.service.category_service;

import java.util.UUID;

public interface CategoryService {

    public String addCategory(String name, UUID parentId);
}
