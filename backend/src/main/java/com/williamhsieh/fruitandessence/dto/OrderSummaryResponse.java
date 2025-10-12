package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.model.OrderDiscount;

import java.math.BigDecimal;
import java.util.List;

public class OrderSummaryResponse {

    private Integer memberId;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private List<OrderDiscount> orderDiscounts;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private List<OrderDiscountResponse> discounts;

    private List<ShoppingListResponse> shoppingListResponse;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public List<OrderDiscount> getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(List<OrderDiscount> orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderDiscountResponse> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<OrderDiscountResponse> discounts) {
        this.discounts = discounts;
    }

    public List<ShoppingListResponse> getShoppingListResponse() {
        return shoppingListResponse;
    }

    public void setShoppingListResponse(List<ShoppingListResponse> shoppingListResponse) {
        this.shoppingListResponse = shoppingListResponse;
    }
}
