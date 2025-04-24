package com.ecommerce.controller.common_controller;

import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import com.ecommerce.service.product_service.ProductService;
import com.ecommerce.utils.service_utils.ProductUtils;
import com.ecommerce.utils.user_utils.CurrentUserUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<ProductResponseDto>> getProductsById(@PathVariable UUID productId) throws FileNotFoundException, BadRequestException {

        ApiResponseDto<ProductResponseDto> response = new ApiResponseDto<>(200,
                "Product Retrieved Successfully",
                productService.getProductByIdForUser(productId,currentUserUtils.getRole())
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ProductResponseDto>>> getProducts(
            @RequestParam(value = "max", defaultValue = "10") int max,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ) throws FileNotFoundException, BadRequestException {
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
        ApiResponseDto<List<ProductResponseDto>> response = new ApiResponseDto<>(200,
                "Products Retrieved Successfully",
                productService.getAllProductsForUser(currentUserUtils.getRole(),max,offset,sort,order,filters)
        );

        return ResponseEntity.ok().body(response);
    }
}
