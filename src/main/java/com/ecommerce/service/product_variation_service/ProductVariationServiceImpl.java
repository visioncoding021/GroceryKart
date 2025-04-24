package com.ecommerce.service.product_variation_service;

import com.ecommerce.dto.request_dto.product_dto.ProductVariationRequestDto;
import com.ecommerce.dto.request_dto.product_dto.ProductVariationUpdateRequestDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_variation_dto.ProductResponseDto;
import com.ecommerce.dto.response_dto.product_variation_dto.ProductVariationResponseDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import com.ecommerce.repository.product_repos.ProductVariationRepository;
import com.ecommerce.service.image_service.ImageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
public class ProductVariationServiceImpl implements ProductVariationService{

    @Autowired
    private ProductVariationValidation productVariationValidation;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private ImageService imageService;

    @Value("${base.path}")
    private String basePath;

    @Override
    @Transactional
    public String addProductVariation(UUID sellerId, ProductVariationRequestDto productVariationRequestDto , Map<String,String> metadata) throws IOException {

        Product product = productVariationValidation.getProductById(productVariationRequestDto.getProductId());

        Category category = product.getCategory();
        if(category==null) throw new BadRequestException("Product doesn't have category. So variation can't be added");
        List<CategoryMetadataFieldValues> categoryMetadataFieldValues = ProductVariationHelper.getMetadataFieldValues(category);

        productVariationValidation.isMetadataValidForProductVariation(metadata, categoryMetadataFieldValues);

        if(product.getProductVariations()!=null && !product.getProductVariations().isEmpty()){
            System.out.println(product.getProductVariations().get(0).getMetadata().keySet().toString());
            productVariationValidation.isStructureSameForAllVariations(product.getProductVariations().get(0).getMetadata().keySet(),metadata.keySet());
        }

        if(product.getProductVariations()!=null && !product.getProductVariations().isEmpty()) productVariationValidation.isMetadataSame(product.getProductVariations(),metadata,null);

        UUID generatedUuid = UUID.randomUUID();

        ProductVariation productVariation = new ProductVariation();
        productVariation.setId(generatedUuid);
        BeanUtils.copyProperties(productVariationRequestDto,productVariation);
        productVariation.setMetadata(metadata);

        productVariation.setProduct(product);

        String path = "/products/" + product.getId() + "/variations" ;

        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(productVariationRequestDto.getPrimaryImage());
        allImages.addAll(Arrays.stream(productVariationRequestDto.getSecondaryImages()).toList());
        imageService.uploadMultipleImages(path,productVariation.getId(),allImages);

        System.out.println(basePath+path+"/"+productVariation.getId());
        productVariation.setPrimaryImageUrl(basePath+path+"/"+productVariation.getId()+".png");

        productVariationRepository.save(productVariation);

        return "Product variation added successfully";
    }

    @Override
    public ProductVariationResponseDto getProductVariationById(UUID productVariationId, UUID sellerId) throws BadRequestException, FileNotFoundException {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new BadRequestException("Product variation not found with ID: " + productVariationId));

        if(!productVariation.getProduct().getSeller().getId().equals(sellerId))
            throw new BadRequestException("Product variation not found with ID: " + productVariationId);

        ProductVariationResponseDto productVariationResponseDto = new ProductVariationResponseDto();
        BeanUtils.copyProperties(productVariation,productVariationResponseDto);

        Product product = productVariation.getProduct();
        ProductResponseDto productResponseDto = new ProductResponseDto();
        BeanUtils.copyProperties(product,productResponseDto);
        productVariationResponseDto.setProduct(productResponseDto);

        String path = "/products/" + product.getId() + "/variations" ;
        List<String> allImages = ProductVariationUtils.getSecondaryImageUrls(basePath+path,imageService.getAllImages(path,productVariation.getId()));
        productVariationResponseDto.setSecondaryImages(allImages);


        return productVariationResponseDto;
    }


    @Override
    public PaginatedResponseDto<List<ProductVariationResponseDto>> getAllProductVariationByProductId(UUID productId, UUID sellerId, int max, int offset, String sort, String order, Map<String, String> filters) throws BadRequestException, FileNotFoundException {
        Product product = productVariationValidation.getProductById(productId);
        if(!product.getSeller().getId().equals(sellerId))
            throw new BadRequestException("Product not found with ID: " + productId);

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Specification<ProductVariation> specification = ProductVariationUtils.getProductVariationFilters(filters,productId);
        Page<ProductVariation> productVariations = productVariationRepository.findAll(specification,pageable);

        List<ProductVariationResponseDto> productVariationResponseDtos = new ArrayList<>();
        for(ProductVariation productVariation : productVariations.getContent()){
            ProductVariationResponseDto productVariationResponseDto = new ProductVariationResponseDto();
            BeanUtils.copyProperties(productVariation,productVariationResponseDto);
            ProductResponseDto productResponseDto = new ProductResponseDto();
            BeanUtils.copyProperties(product,productResponseDto);
            productVariationResponseDto.setProduct(productResponseDto);
            productVariationResponseDtos.add(productVariationResponseDto);
            String path = "/products/" + product.getId() + "/variations" ;
            List<String> allImages = ProductVariationUtils.getSecondaryImageUrls(basePath+path,imageService.getAllImages(path,productVariation.getId()));
            productVariationResponseDto.setSecondaryImages(allImages);
        }

        return ProductVariationUtils.getProductVariationPaginatedResponse(productVariationResponseDtos,productVariations);
    }


    @Override
    public String updateProductVariation(UUID productVariationId, UUID sellerId, ProductVariationUpdateRequestDto productVariationRequestDto, Map<String, String> metadata) throws IOException {

        ProductVariation productVariation = productVariationRepository.findById(productVariationId).orElseThrow(()->new BadRequestException("Product variation not found with ID: " + productVariationId));

        Product product = productVariation.getProduct();
        if(!product.getSeller().getId().equals(sellerId))
            throw new BadRequestException("Product variation not found with ID: " + productVariationId);

        if(!productVariation.getIsActive())
            throw new BadRequestException("Product variation is not active. So it can't be updated");

        Category category = product.getCategory();
        List<CategoryMetadataFieldValues> categoryMetadataFieldValues = ProductVariationHelper.getMetadataFieldValues(category);

        productVariationValidation.isMetadataValidForProductVariation(metadata, categoryMetadataFieldValues);

        if(product.getProductVariations()!=null && !product.getProductVariations().isEmpty()){
            System.out.println(product.getProductVariations().get(0).getMetadata().keySet().toString());
            productVariationValidation.isStructureSameForAllVariations(product.getProductVariations().get(0).getMetadata().keySet(),metadata.keySet());
        }

        if(product.getProductVariations()!=null && !product.getProductVariations().isEmpty()) productVariationValidation.isMetadataSame(product.getProductVariations(),metadata,productVariation);

        BeanUtils.copyProperties(productVariationRequestDto,productVariation);
        productVariation.setProduct(product);
        productVariation.setMetadata(metadata);

        String path = "/products/" + product.getId() + "/variations" ;

        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(productVariationRequestDto.getPrimaryImage());
        imageService.uploadMultipleImages(path,productVariation.getId(),allImages);
        allImages.clear();
        allImages.addAll(Arrays.stream(productVariationRequestDto.getSecondaryImages()).toList());
        imageService.updateMultipleImages(path,productVariation.getId(),allImages);

        System.out.println(basePath+path+"/"+productVariation.getId());
        productVariation.setPrimaryImageUrl(basePath+path+"/"+productVariation.getId()+".png");

        productVariationRepository.save(productVariation);

        return "Product variation updated successfully";
    }

}
