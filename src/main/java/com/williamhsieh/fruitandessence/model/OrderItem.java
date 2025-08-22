package com.williamhsieh.fruitandessence.model;

public class OrderItem {

    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private Integer purchasedCount;
    private Double purchasedWeight;
    private Integer amount;

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
