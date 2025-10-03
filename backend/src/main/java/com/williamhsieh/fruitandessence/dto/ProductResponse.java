package com.williamhsieh.fruitandessence.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.williamhsieh.fruitandessence.constant.ProductCategory;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Integer productId;
    private String productName;
    private ProductCategory productCategory;
    private List<String> productImages;
    private List<ProductVariantResponse> productVariants;
    private List<String> productIngredients;
    private String productDescription;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public List<ProductVariantResponse> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariantResponse> productVariants) {
        this.productVariants = productVariants;
    }

    public List<String> getProductIngredients() {
        return productIngredients;
    }

    public void setProductIngredients(List<String> productIngredients) {
        this.productIngredients = productIngredients;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}

