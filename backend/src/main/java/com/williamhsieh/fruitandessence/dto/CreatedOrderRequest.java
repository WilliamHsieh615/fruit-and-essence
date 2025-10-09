package com.williamhsieh.fruitandessence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreatedOrderRequest {

    @NotBlank
    private Integer memberId;

    @NotBlank
    private String shippingPhone;

    @NotBlank
    private String shippingAddress;

    private String shippingNote;

    @NotNull
    private Integer paymentMethodId;

    @NotNull
    private Integer shippingMethodId;

    private Integer discountId;

    @NotEmpty
    private List<OrderItemRequest> orderItems;

    private InvoiceRequest invoice;

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingNote() {
        return shippingNote;
    }

    public void setShippingNote(String shippingNote) {
        this.shippingNote = shippingNote;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Integer getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(Integer shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }

    public InvoiceRequest getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceRequest invoice) {
        this.invoice = invoice;
    }
}
