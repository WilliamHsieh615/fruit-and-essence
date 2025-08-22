package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.constant.ProductUnitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductRequest {

    @NotNull
    private String productName;

    @NotNull
    private ProductCategory category;

    @NotNull
    private String imageUrl;

    @NotNull
    private Integer stock; // 庫存量

    @NotNull
    private Integer pricePerUnit; // 每單位價格

    @NotNull
    private String unit; // 單位

    @NotNull
    private ProductUnitType unitType; // WEIGHT 或 COUNT

    private Double weight;     // 購買重量
    private Integer count; // 購買數量

    @NotNull
    private String description;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Integer pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ProductUnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(ProductUnitType unitType) {
        this.unitType = unitType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
