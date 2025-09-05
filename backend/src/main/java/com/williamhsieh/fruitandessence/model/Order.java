package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private Integer orderId;
    private Integer memberId;
    private Integer totalAmount;
    private String shippingAddress;
    private String shippingPhone;
    private OrderStatus status;
    private LocalDate orderDate; // 預計訂單完成日期
    private LocalDateTime createdDate; // 訂單建立時間
    private LocalDateTime lastModifiedDate; // 追蹤訂單狀態最後更新的時間，隨著 status 的變更而改變

    private List<OrderItemResponse> orderItemListResponse;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
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

    public List<OrderItemResponse> getOrderItemListResponse() {
        return orderItemListResponse;
    }

    public void setOrderItemListResponse(List<OrderItemResponse> orderItemListResponse) {
        this.orderItemListResponse = orderItemListResponse;
    }
}
