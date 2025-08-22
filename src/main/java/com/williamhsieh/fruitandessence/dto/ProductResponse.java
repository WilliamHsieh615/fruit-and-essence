package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.ProductUnitType;
import com.williamhsieh.fruitandessence.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Integer productId;
    private String productName;
    private String category;
    private String imageUrl;
    private Integer stock;
    private Integer pricePerUnit;
    private String unit;
    private ProductUnitType unitType;
    private Double weight;      // 只有 WEIGHT 類型才會有值
    private Integer count;   // 只有 COUNT 類型才會有值
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Integer totalPrice;

    public ProductResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.category = product.getCategory().name();
        this.imageUrl = product.getImageUrl();
        this.stock = product.getStock();
        this.pricePerUnit = product.getPricePerUnit();
        this.unit = product.getUnit();
        this.unitType = product.getUnitType();
        this.description = product.getDescription();
        this.totalPrice = product.getTotalPrice();

        // 根據 unitType 判斷要回傳哪個欄位
        if (unitType == ProductUnitType.WEIGHT) {
            this.weight = product.getWeight();
        }
        if (unitType == ProductUnitType.COUNT) {
            this.count = product.getCount();
        }

        this.createdDate = product.getCreatedDate();
        this.lastModifiedDate = product.getLastModifiedDate();
    }

    // getters
    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getPricePerUnit() {
        return pricePerUnit;
    }

    public String getUnit() {
        return unit;
    }

    public ProductUnitType getUnitType() {
        return unitType;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }
}

