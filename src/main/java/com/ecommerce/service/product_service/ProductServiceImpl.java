package com.ecommerce.service.product_service;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldValueResponseDto;
import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductVariationResponseDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import com.ecommerce.models.user.Seller;
import com.ecommerce.repository.category_repos.CategoryRepository;
import com.ecommerce.repository.product_repos.ProductRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import com.ecommerce.service.category_service.CategoryMapper;
import com.ecommerce.utils.service_utils.ProductUtils;
import jakarta.persistence.criteria.Predicate;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

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

        return "Product added successfully and Product ID is "+product.getId();
    }

    @Override
    public ProductResponseDto getProductDetailsById(UUID productId, UUID sellerId) throws BadRequestException {
        Product product = productRepository.findByIdAndSellerId(productId,sellerId).orElseThrow(() -> new BadRequestException("Product not found with ID: " + productId));
        ProductResponseDto productResponseDto = new ProductResponseDto();
        BeanUtils.copyProperties(product,productResponseDto);

        Category category = product.getCategory();
        productResponseDto.setCategory(getCategoryResponse(category));
        productResponseDto.setProductVariations(getProductVariationResponseDtos(product));

        return productResponseDto;
    }

    @Override
    public PaginatedResponseDto<List<ProductResponseDto>> getAllProductsBySellerId(UUID sellerId, int max, int offset, String sort, String order, Map<String, String> filters) throws BadRequestException {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Specification<Product> specification = ProductUtils.getProductFilters(filters,sellerId);
        Page<Product> products = productRepository.findAll(specification,pageable);
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for(Product product : products.getContent()){
            ProductResponseDto productResponseDto = new ProductResponseDto();
            BeanUtils.copyProperties(product,productResponseDto);
            Category category = product.getCategory();
            productResponseDto.setCategory(getCategoryResponse(category));
            productResponseDto.setProductVariations(getProductVariationResponseDtos(product));
            productResponseDtos.add(productResponseDto);
        }
        return ProductUtils.getProductPaginatedResponse(productResponseDtos,products);
    }

    private LeafCategoryResponseDto getCategoryResponse(Category category){
        LeafCategoryResponseDto categoryResponseDto = new LeafCategoryResponseDto();
        BeanUtils.copyProperties(category,categoryResponseDto);
        categoryResponseDto.setParent(categoryMapper.mapParentHierarchyAndMetadataFieldValuesForLeaf(category,categoryResponseDto));
        List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = categoryMapper.mapFields(category);
        List<CategoryMetadataFieldValueResponseDto> currentFields = categoryResponseDto.getFields();
        if (!fieldsResponseDtos.isEmpty()) currentFields.addAll(fieldsResponseDtos);
        categoryResponseDto.setFields(currentFields);
        return categoryResponseDto;
    }

    private List<ProductVariationResponseDto> getProductVariationResponseDtos(Product product){
        List<ProductVariationResponseDto> productVariationResponseDtos = new ArrayList<>();
        for(ProductVariation productVariation : product.getProductVariations()){
            ProductVariationResponseDto productVariationResponseDto = new ProductVariationResponseDto();
            BeanUtils.copyProperties(productVariation,productVariationResponseDto);
            productVariationResponseDtos.add(productVariationResponseDto);
            productVariationResponseDto.setProductId(product.getId());
        }
        return productVariationResponseDtos;
    }


}
