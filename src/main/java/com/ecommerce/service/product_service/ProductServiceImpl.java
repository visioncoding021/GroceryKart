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
import com.ecommerce.repository.product_repos.ProductVariationRepository;
import com.ecommerce.repository.user_repos.SellerRepository;
import com.ecommerce.service.category_service.CategoryMapper;
import com.ecommerce.service.image_service.ImageService;
import com.ecommerce.utils.service_utils.ProductUtils;
import com.ecommerce.utils.service_utils.ProductVariationUtils;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
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
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ImageService imageService;

    @Value("${base.path}")
    private String basePath;

    @Override
    @Transactional
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

    @Transactional
    @Override
    public String deleteProduct(UUID productId, UUID sellerId) throws BadRequestException {
        Product product = productRepository.findByIdAndSellerId(productId,sellerId).orElseThrow(() -> new BadRequestException("Product not found with ID: " + productId));
        if(product.getIsDeleted()==true)
            throw new BadRequestException("Product with id " + productId + " doesn't exists");

        for (ProductVariation productVariation : product.getProductVariations()){
            productVariation.setIsActive(false);
            productVariationRepository.save(productVariation);
        }
        product.setIsDeleted(true);
        productRepository.save(product);
        return "Product with id " + productId + " is deleted successfully";
    }

    @Override
    public ProductResponseDto getProductDetailsById(UUID productId, UUID sellerId) throws BadRequestException, FileNotFoundException {
        Product product = productRepository.findByIdAndSellerId(productId,sellerId).orElseThrow(() -> new BadRequestException("Product not found with ID: " + productId));
        if(product.getIsDeleted()==true)
            throw new BadRequestException("Product with id " + productId + " doesn't exists");

        ProductResponseDto productResponseDto = new ProductResponseDto();
        BeanUtils.copyProperties(product,productResponseDto);

        Category category = product.getCategory();
        productResponseDto.setCategory(getCategoryResponse(category));
        productResponseDto.setProductVariations(getProductVariationResponseDtos(product));

        return productResponseDto;
    }

    @Override
    public PaginatedResponseDto<List<ProductResponseDto>> getAllProductsBySellerId(UUID sellerId, int max, int offset, String sort, String order, Map<String, String> filters) throws BadRequestException, FileNotFoundException {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Specification<Product> specification = ProductUtils.getProductFiltersForSeller(filters,sellerId);
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

    @Override
    @Transactional
    public String updateProduct(ProductRequestDto productRequestDto, UUID productId, UUID sellerId) throws BadRequestException {
        String name = productRequestDto.getName().trim();
        String brand = productRequestDto.getBrand();
        UUID categoryId = productRequestDto.getCategoryId();

        Product product = productRepository.findByIdAndSellerId(productId,sellerId).orElseThrow(() -> new BadRequestException("Product not found with ID: " + productId));
        if(product.getIsActive()==false || product.getIsDeleted()==true)
            throw new BadRequestException("Product with id " + productId + " is not active or deleted");

        if(productRepository.existsByNameIgnoreCaseAndBrandAndCategoryIdAndSellerId(name,brand,categoryId,sellerId))
            throw new BadRequestException("Can't Update product as it's already exists with same name");

        BeanUtils.copyProperties(productRequestDto,product);
        product.setIsActive(false);

        productRepository.save(product);
        return "Product updated successfully";
    }

    @Override
    @Transactional
    public String activateProduct(UUID productId) throws BadRequestException {
        if (!productRepository.existsByIdAndIsDeleted(productId,false)) {
            throw new BadRequestException("Product with id " + productId + " doesn't exists");
        }
        if (productRepository.existsByIdAndIsActive(productId, true)) {
            throw new BadRequestException("Product with id " + productId + " is already active");
        }

        Product product = productRepository.findById(productId).get();
        product.setIsActive(true);
        productRepository.save(product);
        return "Product with id " + productId + " is now active";
    }

    @Override
    @Transactional
    public String deactivateProduct(UUID productId) throws BadRequestException {
        if (!productRepository.existsByIdAndIsDeleted(productId,false)) {
            throw new BadRequestException("Product with id " + productId + " doesn't exists");
        }
        if (!productRepository.existsByIdAndIsActive(productId, true)) {
            throw new BadRequestException("Product with id " + productId + " is already deactivated");
        }

        Product product = productRepository.findById(productId).get();
        product.setIsActive(false);
        productRepository.save(product);
        return "Product with id " + productId + " is now deactivated";
    }

    @Override
    public ProductResponseDto getProductByIdForUser(UUID productId, String role) throws BadRequestException, FileNotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BadRequestException("Product not found with ID: " + productId));

        if(role.equals("ROLE_CUSTOMER") && (product.getIsActive()==false || product.getIsDeleted()==true))
            throw new BadRequestException("Product with id " + productId + " is not active or deleted");

        if(role.equals("ROLE_CUSTOMER") && (product.getProductVariations().isEmpty()))
            throw new BadRequestException("Product with id " + productId + " has no product Variations");

        ProductResponseDto productResponseDto = new ProductResponseDto();
        BeanUtils.copyProperties(product,productResponseDto);
        Category category = product.getCategory();
        productResponseDto.setCategory(getCategoryResponse(category));
        productResponseDto.setProductVariations(getProductVariationResponseDtos(product));
        return productResponseDto;
    }

    @Override
    public PaginatedResponseDto<List<ProductResponseDto>> getAllProductsForUser(String role,UUID categoryId, int max, int offset, String sort, String order, Map<String, String> filters) throws BadRequestException, FileNotFoundException {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Specification<Product> specification = null;
        if (role.equals("ROLE_CUSTOMER")){
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category not found with ID: " + categoryId));
            List<String> categoryIdList = null;
            if(category.getSubCategories()!=null && !category.getSubCategories().isEmpty())
                categoryIdList = getLeafCategoriesByCategory(category);
            else filters.put("categoryId", categoryId.toString());
            specification = ProductUtils.getProductFiltersForCustomer(filters,categoryIdList);
        }
        else if (role.equals("ROLE_ADMIN")) specification = ProductUtils.getProductFiltersForAdmin(filters);
        Page<Product> products = productRepository.findAll(specification,pageable);
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for(Product product : products.getContent()){
            if(role.equals("ROLE_CUSTOMER") && (product.getProductVariations().isEmpty()))
                continue;
            ProductResponseDto productResponseDto = new ProductResponseDto();
            BeanUtils.copyProperties(product,productResponseDto);
            Category category = product.getCategory();
            productResponseDto.setCategory(getCategoryResponse(category));
            productResponseDto.setProductVariations(getProductVariationResponseDtos(product));
            productResponseDtos.add(productResponseDto);
        }
        return ProductUtils.getProductPaginatedResponse(productResponseDtos,products);
    }

    @Override
    public PaginatedResponseDto<List<ProductResponseDto>> getAllSimilarProductsForCustomer(UUID productId, int max, int offset, String sort, String order, Map<String, String> filters) throws FileNotFoundException {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));
        Product product1 = productRepository.findById(productId).get();
        filters.put("categoryId", product1.getCategory().getId().toString());
        Specification<Product> specification = ProductUtils.getProductFiltersForCustomer(filters,null);
        Page<Product> products = productRepository.findAll(specification,pageable);
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for(Product product : products.getContent()){
            if (product.getId().equals(product1.getId()) || product.getProductVariations().isEmpty())
                continue;
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
        categoryResponseDto.setParent(categoryMapper.mapParentHierarchyAndMetadataFieldValuesForLeaf(category.getParent(),categoryResponseDto));
        List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = categoryMapper.mapFields(category);
        List<CategoryMetadataFieldValueResponseDto> currentFields = categoryResponseDto.getFields();
        if (!fieldsResponseDtos.isEmpty()) currentFields.addAll(fieldsResponseDtos);
        categoryResponseDto.setFields(currentFields);
        return categoryResponseDto;
    }

    private List<ProductVariationResponseDto> getProductVariationResponseDtos(Product product) throws FileNotFoundException {
        List<ProductVariationResponseDto> productVariationResponseDtos = new ArrayList<>();
        for(ProductVariation productVariation : product.getProductVariations()){
            ProductVariationResponseDto productVariationResponseDto = new ProductVariationResponseDto();
            BeanUtils.copyProperties(productVariation,productVariationResponseDto);
            productVariationResponseDtos.add(productVariationResponseDto);
            productVariationResponseDto.setProductId(product.getId());
            String path = "/products/" + product.getId() + "/variations" ;
            List<String> allImages = ProductVariationUtils.getSecondaryImageUrls(basePath+path,imageService.getAllImages(path,productVariation.getId()));
            productVariationResponseDto.setSecondaryImages(allImages);
        }
        return productVariationResponseDtos;
    }

    private List<String> getLeafCategoriesByCategory(Category category) {
        List<String> leafIds = new ArrayList<>();
        collectLeafCategoryIds(category, leafIds);
        return leafIds;
    }

    private void collectLeafCategoryIds(Category category, List<String> leafIds) {
        if (category.getSubCategories() == null || category.getSubCategories().isEmpty()) {
            leafIds.add(category.getId().toString());
            return;
        }
        for (Category sub : category.getSubCategories()) {
            collectLeafCategoryIds(sub, leafIds);
        }
    }

}
