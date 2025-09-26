package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.ProductSize;

import java.math.BigDecimal;

public class ProductVariant {

    private Integer productVariantId;
    private Integer productId;
    private ProductSize productSize; // 規格
    private BigDecimal price; // 價格
    private BigDecimal discountPrice; // 折扣後價格
    private String unit; // 單位
    private Integer stock; // 庫存
    private String sku;
    private String barcode;

}
