package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.OrderStatus;

import java.time.LocalDateTime;

public class Order {

    private Integer orderId;
    private Integer memberId;
    private Integer totalAmount;
    private String shippingAddress;
    private String shippingPhone;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime created_date;
    private LocalDateTime last_modified_date;

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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDateTime created_date) {
        this.created_date = created_date;
    }

    public LocalDateTime getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(LocalDateTime last_modified_date) {
        this.last_modified_date = last_modified_date;
    }
}
