package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.DiscountType;
import com.williamhsieh.fruitandessence.constant.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDiscount {

    private Integer discountId;
    private Integer memberId;
    private String discountName;
    private String discountCode;

    private DiscountType discountType; // 折扣類型 ENUM
    private BigDecimal discountValue; // 固定金額折扣
    private BigDecimal discountPercentage; // 百分比折扣

    private BigDecimal minOrderAmount; // 訂單門檻
    private Integer totalUsageLimit; // 可用次數
    private List<ProductCategory> allowedCategories; // 允許使用產品類型
    private List<Integer> allowedProductIds; // 允許使用產品
    private List<Role> allowedRoles; // 允許使用的角色

    private LocalDateTime startDate; // 折扣開始時間
    private LocalDateTime endDate; // 折扣結束時間

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getTotalUsageLimit() {
        return totalUsageLimit;
    }

    public void setTotalUsageLimit(Integer totalUsageLimit) {
        this.totalUsageLimit = totalUsageLimit;
    }

    public List<ProductCategory> getAllowedCategories() {
        return allowedCategories;
    }

    public void setAllowedCategories(List<ProductCategory> allowedCategories) {
        this.allowedCategories = allowedCategories;
    }

    public List<Integer> getAllowedProductIds() {
        return allowedProductIds;
    }

    public void setAllowedProductIds(List<Integer> allowedProductIds) {
        this.allowedProductIds = allowedProductIds;
    }

    public List<Role> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<Role> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
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
