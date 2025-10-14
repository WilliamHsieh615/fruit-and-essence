package com.williamhsieh.fruitandessence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class CreatedOrderRequest {

    @NotNull
    private Integer memberId;

    @NotNull
    private List<CreatedOrderItemRequest> createdOrderItems;

    @NotNull
    @Positive
    private BigDecimal subtotal;

    @NotNull
    private BigDecimal taxAmount;

    private List<String> discountCodes;

    private BigDecimal discountAmount;

    @NotNull
    private BigDecimal shippingFee;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    @NotBlank
    private String shippingPhone;

    @NotBlank
    private String shippingAddress;

    private String shippingNote;

    @NotNull
    private Integer paymentMethodId;

    @NotNull
    private Integer shippingMethodId;

    @NotNull
    private InvoiceRequest invoiceRequest;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public List<CreatedOrderItemRequest> getCreatedOrderItems() {
        return createdOrderItems;
    }

    public void setCreatedOrderItems(List<CreatedOrderItemRequest> createdOrderItems) {
        this.createdOrderItems = createdOrderItems;
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

    public List<String> getDiscountCodes() {
        return discountCodes;
    }

    public void setDiscountCodes(List<String> discountCodes) {
        this.discountCodes = discountCodes;
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

    public InvoiceRequest getInvoiceRequest() {
        return invoiceRequest;
    }

    public void setInvoiceRequest(InvoiceRequest invoiceRequest) {
        this.invoiceRequest = invoiceRequest;
    }
}
