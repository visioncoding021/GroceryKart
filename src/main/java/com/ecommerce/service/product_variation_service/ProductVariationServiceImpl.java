package com.ecommerce.service.product_variation_service;

import com.ecommerce.dto.request_dto.product_dto.ProductVariationRequestDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import com.ecommerce.repository.product_repos.ProductVariationRepository;
import com.ecommerce.service.image_service.ImageService;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        UUID generatedUuid = UUID.randomUUID();

        ProductVariation productVariation = new ProductVariation();
        productVariation.setId(generatedUuid);
        BeanUtils.copyProperties(productVariationRequestDto,productVariation);
        productVariation.setProduct(product);

        String path = "/products/" + product.getId() + "/variations" ;

        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(productVariationRequestDto.getPrimaryImage());
        allImages.addAll(Arrays.stream(productVariationRequestDto.getSecondaryImages()).toList());
        imageService.uploadMultipleImages(path,productVariation.getId(),allImages);

        System.out.println(basePath+path+"/"+productVariation.getId());
        productVariation.setPrimaryImageUrl(basePath+path+"/"+productVariation.getId());

        productVariationRepository.save(productVariation);

        return "Product variation added successfully";
    }


}
