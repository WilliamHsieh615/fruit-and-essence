package com.williamhsieh.fruitandessence.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class PurchaseItem {

    @NotNull
    private Integer productId;

    private Integer purchasedCount;
    private Double purchasedWeight;

    @AssertTrue(message = "Specify either purchase count or purchase weight.")
    public boolean isValid() {
        boolean hasCount = purchasedCount != null && purchasedCount > 0;
        boolean hasWeight = purchasedWeight != null && purchasedWeight > 0;
        return hasCount ^ hasWeight; // XOR：只能其中一個為 true
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPurchasedCount() {
        return purchasedCount;
    }

    public void setPurchasedCount(Integer purchasedCount) {
        this.purchasedCount = purchasedCount;
    }

    public Double getPurchasedWeight() {
        return purchasedWeight;
    }

    public void setPurchasedWeight(Double purchasedWeight) {
        this.purchasedWeight = purchasedWeight;
    }
}
