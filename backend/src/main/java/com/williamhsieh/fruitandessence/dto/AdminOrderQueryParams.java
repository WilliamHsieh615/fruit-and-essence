package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.OrderStatus;

import java.time.LocalDateTime;

public class AdminOrderQueryParams {

    private OrderStatus orderStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer limit = 50;
    private Integer offset = 0;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
