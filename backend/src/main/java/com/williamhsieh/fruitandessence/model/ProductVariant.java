package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.ProductSize;

import java.math.BigDecimal;
import java.util.List;

public class ProductVariant {

    private Integer productVariantId;
    private Integer productId;
    private ProductSize productSize; // 規格
    private BigDecimal price; // 價格
    private BigDecimal discountPrice; // 折扣後價格
    private String unit; // 單位
    private Integer stock; // 庫存
    private String sku; // 商品貨號
    private String barcode; // 商品條碼
    private List<StockHistory> stockHistoryList;

    public Integer getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Integer productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public ProductSize getProductSize() {
        return productSize;
    }

    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<StockHistory> getStockHistoryList() {
        return stockHistoryList;
    }

    public void setStockHistoryList(List<StockHistory> stockHistoryList) {
        this.stockHistoryList = stockHistoryList;
    }
}
