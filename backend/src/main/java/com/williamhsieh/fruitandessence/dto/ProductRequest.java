package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ProductRequest {

    @NotBlank
    private String productName;

    @NotNull
    private ProductCategory productCategory;

    @NotEmpty
    private List<String> productImages;

    @NotEmpty
    private List<ProductVariantRequest> productVariants;

    @NotEmpty
    private List<String> productIngredients;

    @NotNull
    private ProductNutritionFactsRequest productNutritionFacts;

    private String productDescription;

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

    public List<ProductVariantRequest> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariantRequest> productVariants) {
        this.productVariants = productVariants;
    }

    public List<String> getProductIngredients() {
        return productIngredients;
    }

    public void setProductIngredients(List<String> productIngredients) {
        this.productIngredients = productIngredients;
    }

    public ProductNutritionFactsRequest getProductNutritionFacts() {
        return productNutritionFacts;
    }

    public void setProductNutritionFacts(ProductNutritionFactsRequest productNutritionFacts) {
        this.productNutritionFacts = productNutritionFacts;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
