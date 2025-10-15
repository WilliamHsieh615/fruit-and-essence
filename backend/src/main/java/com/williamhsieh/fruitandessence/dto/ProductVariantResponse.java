package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.ProductSize;

import java.math.BigDecimal;

public class ProductVariantResponse {

    private Integer productVariantId;
    private ProductSize productSize;
    private String productSizeLabel;
    private Integer productSizeFluidOunce;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String unit;
    private Integer stock;
    private String sku;
    private String barcode;

    private ProductNutritionFactsResponse nutritionFacts;

    public Integer getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Integer productVariantId) {
        this.productVariantId = productVariantId;
    }

    public ProductSize getProductSize() {
        return productSize;
    }

    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    public String getProductSizeLabel() {
        return productSizeLabel;
    }

    public void setProductSizeLabel(String productSizeLabel) {
        this.productSizeLabel = productSizeLabel;
    }

    public Integer getProductSizeFluidOunce() {
        return productSizeFluidOunce;
    }

    public void setProductSizeFluidOunce(Integer productSizeFluidOunce) {
        this.productSizeFluidOunce = productSizeFluidOunce;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ProductNutritionFactsResponse getNutritionFacts() {
        return nutritionFacts;
    }

    public void setNutritionFacts(ProductNutritionFactsResponse nutritionFacts) {
        this.nutritionFacts = nutritionFacts;
    }
}
