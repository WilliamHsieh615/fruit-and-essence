package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.constant.ProductUnitType;

import java.time.LocalDateTime;

public class Product {

    private Integer productId;
    private String productName;
    private ProductCategory category;
    private String imageUrl;

    private Double stock; // 庫存量
    private Integer pricePerUnit; // 每單位價格
    private String unit; // 單位
    private ProductUnitType unitType; // WEIGHT 或 COUNT
    private Double weight; // 單位重量 1 kg
    private Integer count; // 單位數量 1 piece

    private String description;
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

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
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

    public int getTotalPrice() {
        if (unitType == ProductUnitType.WEIGHT) {
            return (int) Math.round((weight == null ? 0 : weight) * pricePerUnit);
        } else {
            return (count == null ? 0 : count * pricePerUnit);
        }
    }
}
