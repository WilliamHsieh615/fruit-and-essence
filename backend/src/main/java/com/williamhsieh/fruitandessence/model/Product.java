package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.constant.ProductSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Product {

    private Integer productId;
    private String productName;
    private ProductCategory productCategory; // ENUM 產品類別
    private List<String> productImages;
    private List<ProductVariant> productVariants; // 產品變體
    private List<String> productIngredients; // 成分
    private ProductNutritionFacts productNutritionFacts; // 營養資訊
    private String productDescription; // 描述
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

    public List<ProductVariant> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariant> productVariants) {
        this.productVariants = productVariants;
    }

    public List<String> getProductIngredients() {
        return productIngredients;
    }

    public void setProductIngredients(List<String> productIngredients) {
        this.productIngredients = productIngredients;
    }

    public ProductNutritionFacts getProductNutritionFacts() {
        return productNutritionFacts;
    }

    public void setProductNutritionFacts(ProductNutritionFacts productNutritionFacts) {
        this.productNutritionFacts = productNutritionFacts;
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
