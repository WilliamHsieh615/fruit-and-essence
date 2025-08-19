package com.williamhsieh.fruitandessence.controller;

import com.williamhsieh.fruitandessence.constant.ProductUnitType;
import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.model.Product;
import com.williamhsieh.fruitandessence.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {

        // 預設 unit
        if (productRequest.getUnit() == null) {
            productRequest.setUnit(
                    productRequest.getUnitType() == ProductUnitType.WEIGHT ? "kg" : "piece"
            );
        }

        // weight / quantity 互斥
        if (productRequest.getUnitType() == ProductUnitType.WEIGHT) {
            if (productRequest.getWeight() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
            productRequest.setQuantity(null); // 清掉不必要欄位
        } else {
            if (productRequest.getQuantity() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
            productRequest.setWeight(null); // 清掉不必要欄位
        }

        Integer productId = productService.createProduct(productRequest);
        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
}
