package com.williamhsieh.fruitandessence.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderSummaryResponse {

    private Integer memberId;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;

    private List<OrderDiscountResponse> orderDiscountResponseList;
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

    public List<OrderDiscountResponse> getOrderDiscountResponseList() {
        return orderDiscountResponseList;
    }

    public void setOrderDiscountResponseList(List<OrderDiscountResponse> orderDiscountResponseList) {
        this.orderDiscountResponseList = orderDiscountResponseList;
    }

    public List<ShoppingListResponse> getShoppingListResponse() {
        return shoppingListResponse;
    }

    public void setShoppingListResponse(List<ShoppingListResponse> shoppingListResponse) {
        this.shoppingListResponse = shoppingListResponse;
    }
}
