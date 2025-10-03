package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.StockChangeReason;

public class StockHistoryRequest {

    private Integer productVariantId;      // 哪個產品規格
    private Integer changeAmount;          // 增減庫存 +10 或 -5
    private StockChangeReason stockChangeReason;      // 變動原因

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

    public StockChangeReason getStockChangeReason() {
        return stockChangeReason;
    }

    public void setStockChangeReason(StockChangeReason stockChangeReason) {
        this.stockChangeReason = stockChangeReason;
    }
}
