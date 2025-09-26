package com.williamhsieh.fruitandessence.model;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.constant.ProductSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Product {

    private Integer productId;
    private String productName;
    private ProductCategory productCategory; // 產品類別
    private List<String> productImages;
    private List<ProductVariant> productVariants; // 產品種類
    private List<String> productIngredients; // 成分
    private ProductNutritionFacts productNutritionFacts; // 營養資訊
    private String productDescription; // 描述
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

}
