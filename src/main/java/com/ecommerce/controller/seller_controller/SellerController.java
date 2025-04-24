package com.ecommerce.controller.seller_controller;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.dto.request_dto.product_dto.ProductVariationRequestDto;
import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import com.ecommerce.dto.response_dto.product_variation_dto.ProductVariationResponseDto;
import com.ecommerce.service.category_service.CategoryService;
import com.ecommerce.service.product_service.ProductService;
import com.ecommerce.service.product_variation_service.ProductVariationService;
import com.ecommerce.utils.service_utils.ProductUtils;
import com.ecommerce.utils.user_utils.CurrentUserUtils;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private ProductVariationService productVariationService;

    @GetMapping("/categories")
    public ApiResponseDto<List<LeafCategoryResponseDto>> getAllLeafCategories(){
        System.out.println(currentUserUtils.getUserId());
        System.out.println(currentUserUtils.getUsername());
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "All Leaf Categories are fetched Successfully",
                categoryService.getAllLeafCategories()
        );
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> addProduct(@Valid @RequestBody ProductRequestDto productRequestDto) throws BadRequestException {
        return ResponseEntity.ok().body(
            productService.addProduct(productRequestDto,currentUserUtils.getUserId())
        );
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<MessageResponseDto> updateProduct(@PathVariable UUID productId, @RequestBody ProductRequestDto productRequestDto) throws BadRequestException {
        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                productService.updateProduct(productRequestDto,productId,currentUserUtils.getUserId())
        );
        return ResponseEntity.ok().body(messageResponseDto);
    }

    @PostMapping("/add-product-variation")
    public ResponseEntity<?> addProductVariation(@Valid @ModelAttribute ProductVariationRequestDto requestDto) throws IOException {
        System.out.println(requestDto.toString());
        Map<String,String> metadata = ProductUtils.parseMetadata(requestDto.getMetadata());
        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                productVariationService.addProductVariation(currentUserUtils.getUserId(),requestDto,metadata)
        );
        return ResponseEntity.ok().body(messageResponseDto);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable UUID productId) throws BadRequestException, FileNotFoundException {
        return ResponseEntity.ok().body(
                productService.getProductDetailsById(productId,currentUserUtils.getUserId())
        );
    }

    @GetMapping("/products/variations/{productVariationId}")
    public ResponseEntity<?> getProductVariation(@PathVariable UUID productVariationId) throws BadRequestException, FileNotFoundException {
        return ResponseEntity.ok().body(
                productVariationService.getProductVariationById(productVariationId,currentUserUtils.getUserId())
        );
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = "max", defaultValue = "10") int max,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ) throws BadRequestException, FileNotFoundException {
        final Set<String> VALID_SORT_FIELDS = Set.of(
                "id", "name", "description", "brand",
                "isCancellable", "isReturnable", "isActive"
        );
        Map<String,String> errors = ProductUtils.validateProductRequestParams(order,max,offset);
        if (!VALID_SORT_FIELDS.contains(sort)) {
            errors.put("Sort" , "Invalid sort field: " + sort);
        }
        if(!errors.isEmpty()){
            throw new BadRequestException(errors.toString());
        }

        Map<String, String> filters = ProductUtils.parseQuery(query);

        PaginatedResponseDto<List<ProductResponseDto>> responseDto = productService.getAllProductsBySellerId(currentUserUtils.getUserId(),max,offset,sort,order,filters);

        responseDto.setMessage("All Products  are fetched successfully");
        return ResponseEntity.ok().body(
                responseDto
        );
    }

    @GetMapping("/products/{productId}/variations")
    public ResponseEntity<?> getAllProductVariations(
            @PathVariable UUID productId,
            @RequestParam(value = "max", defaultValue = "10") int max,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ) throws BadRequestException, FileNotFoundException {
        final Set<String> VALID_SORT_FIELDS = Set.of(
                "id", "quantityAvailable", "price", "primaryImageUrl", "isActive"
        );
        Map<String,String> errors = ProductUtils.validateProductRequestParams(order,max,offset);
        if (!VALID_SORT_FIELDS.contains(sort)) {
            errors.put("Sort" , "Invalid sort field: " + sort);
        }
        if(!errors.isEmpty()){
            throw new BadRequestException(errors.toString());
        }

        Map<String, String> filters = ProductUtils.parseQuery(query);
        System.out.printf("Filters: %s", filters);

        PaginatedResponseDto<List<ProductVariationResponseDto>> responseDto =  productVariationService.getAllProductVariationByProductId(productId,currentUserUtils.getUserId(),max,offset,sort,order,filters);
        responseDto.setMessage("All Product Variations are fetched successfully");
        return ResponseEntity.ok().body(
            responseDto
        );
    }

}
