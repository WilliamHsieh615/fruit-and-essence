package com.williamhsieh.fruitandessence.dto;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.constant.ProductSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ShoppingListResponse {

    private Integer productId;
    private Integer productVariantId;

    private String productImage;
    private ProductCategory productCategory;
    private String productName;
    private ProductSize productSize;
    private Integer quantity;
    private String unit;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal itemTotal; // = discountPrice ? quantity * discountPrice : quantity * price



}
