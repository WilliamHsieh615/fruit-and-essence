package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.model.OrderDiscount;

import java.math.BigDecimal;
import java.util.List;

public class OrderSummaryResponse {

    private Integer memberId;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private OrderDiscount orderDiscount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private List<OrderDiscount> discounts;

    private List<ShoppingListResponse> shoppingListResponse;


}
