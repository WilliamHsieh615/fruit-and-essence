package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.DiscountType;

import java.math.BigDecimal;

public class OrderDiscount {

    private Integer discountId;
    private String discountName;
    private DiscountType discountType;
    private BigDecimal discountValue;

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
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
}
