package com.williamhsieh.fruitandessence.model;

import java.math.BigDecimal;

public class ProductNutritionFacts {

    private Integer productNutritionId;
    private Integer productId;
    private String servingSize; // 份量大小，用於「每份含」
    private Integer calories; // 熱量 (kcal)
    private BigDecimal protein; // 蛋白質 (克, g)
    private BigDecimal fat; // 脂肪 (克, g)
    private BigDecimal carbohydrates; // 碳水化合物 (克, g)
    private BigDecimal sugar; // 糖 (克, g)
    private BigDecimal fiber; // 纖維 (克, g)
    private BigDecimal sodium; // 鈉含量 (毫克, mg)
    private BigDecimal vitaminC; // 維生素 C 含量 (毫克, mg)

}
