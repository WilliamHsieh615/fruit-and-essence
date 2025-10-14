package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.StockChangeReason;

import java.time.LocalDateTime;

public class StockHistory {

    private Integer stockHistoryId;
    private Integer productVariantId;
    private Integer changeAmount; // 增減量
    private Integer stockAfter; // 異動後剩餘
    private StockChangeReason stockChangeReason; // 變動原因
    private LocalDateTime createdDate;

    public Integer getStockHistoryId() {
        return stockHistoryId;
    }

    public void setStockHistoryId(Integer stockHistoryId) {
        this.stockHistoryId = stockHistoryId;
    }

    public Integer getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Integer productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Integer getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Integer changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(Integer stockAfter) {
        this.stockAfter = stockAfter;
    }

    public StockChangeReason getStockChangeReason() {
        return stockChangeReason;
    }

    public void setStockChangeReason(StockChangeReason stockChangeReason) {
        this.stockChangeReason = stockChangeReason;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
