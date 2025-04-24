package com.ecommerce.controller.customer_controller;

import com.ecommerce.dto.response_dto.category_dto.CategoryNameResponseDto;
import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.service.category_service.CategoryService;
import com.ecommerce.service.product_service.ProductService;
import com.ecommerce.utils.service_utils.ProductUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public ApiResponseDto<List<CategoryNameResponseDto>> getAllCategories(
            @RequestParam(value = "categoryId", defaultValue = "") String id
    ){
        UUID categoryId = (id.isBlank())?null:UUID.fromString(id);
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "All Categories are fetched Successfully",
                categoryService.getSameLevelCategories(categoryId)
        );
    }

    @GetMapping("/{productId}/similar-products")
    public ApiResponseDto<List<LeafCategoryResponseDto>> getSimilarProducts(
            @PathVariable UUID productId,
            @RequestParam(value = "max", defaultValue = "10") int max,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ) throws BadRequestException, FileNotFoundException {

        final Set<String> VALID_SORT_FIELDS = Set.of(
                "id", "quantityAvailable", "price", "isActive"
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
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "All Similar Products are fetched Successfully",
                productService.getAllSimilarProductsForCustomer(productId, max, offset, sort, order, filters)
        );
    }
}
