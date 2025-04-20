package com.ecommerce.service.product_service;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.user.Seller;
import com.ecommerce.repository.category_repos.CategoryRepository;
import com.ecommerce.repository.product_repos.ProductRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public String addProduct(ProductRequestDto productRequestDto, UUID sellerId) throws BadRequestException {
        Seller seller = sellerRepository.findById(sellerId).get();
        String name = productRequestDto.getName().trim();
        String brand = productRequestDto.getBrand();
        UUID categoryId = productRequestDto.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new BadRequestException("Category Doesn't exists under which you are adding product"));

        if(category.getSubCategories()!=null && !category.getSubCategories().isEmpty())
            throw new BadRequestException("Category id is not leaf category . So product ant be added under this category");

        if(productRepository.existsByNameIgnoreCaseAndBrandAndCategoryIdAndSellerId(name,brand,categoryId,sellerId))
            throw new BadRequestException("Can't add product as it's already exists with same name");

        Product product = new Product();
        BeanUtils.copyProperties(productRequestDto,product);
        product.setCategory(category);
        product.setSeller(seller);
        product.setIsActive(false);
        productRepository.save(product);

        return "Product added successfully";
    }
}
