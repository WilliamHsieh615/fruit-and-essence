package com.williamhsieh.fruitandessence.controller;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.constant.ProductUnitType;
import com.williamhsieh.fruitandessence.dto.ProductQueryParams;
import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.dto.ProductResponse;
import com.williamhsieh.fruitandessence.service.ProductService;
import com.williamhsieh.fruitandessence.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForOffsetTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getProducts(

            // 查詢條件 Filtering
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,

            // 排序 Sorting
            @RequestParam(defaultValue = "productName") String orderBy,
            @RequestParam(defaultValue = "asc") String sort, // asc升序、desc降序

            // 分頁 Pagination
            @RequestParam(defaultValue = "8") @Max(1000) @Min(0) Integer limit, // 取得的資料數，最大取得筆數為 1000，最小為 0
            @RequestParam(defaultValue = "0") @Min(0) Integer offset // 跳過前幾筆數據，最小為 0

    ) {

        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

        // 取得 product list
        List<ProductResponse> productList = productService.getProducts(productQueryParams);

        // 取得 product 總數
        Integer total = productService.countProduct(productQueryParams);

        // 分頁
        Page<ProductResponse> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(productList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Integer productId) {
        ProductResponse product = productService.getProductById(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {

        // 預設 unit
        if (productRequest.getUnit() == null) {
            productRequest.setUnit(
                    productRequest.getUnitType() == ProductUnitType.WEIGHT ? "kg" : "piece"
            );
        }

        // weight / count 互斥
        if (productRequest.getUnitType() == ProductUnitType.WEIGHT) {
            if (productRequest.getWeight() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
            productRequest.setCount(null); // 清掉不必要欄位
        } else {
            if (productRequest.getCount() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
            productRequest.setWeight(null); // 清掉不必要欄位
        }

        Integer productId = productService.createProduct(productRequest);
        ProductResponse product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Integer productId, @RequestBody @Valid ProductRequest productRequest) {

        // 先檢查 product 是否存在
        ProductResponse product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            productService.updateProduct(productId, productRequest);
            ProductResponse updateProduct = productService.getProductById(productId);
            return ResponseEntity.status(HttpStatus.OK).body(updateProduct);
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {

        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

}
