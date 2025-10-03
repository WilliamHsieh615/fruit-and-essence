package com.williamhsieh.fruitandessence.dto;

import java.math.BigDecimal;

public class ProductNutritionFactsResponse {

    private String servingSize; // 份量大小，用於「每份含」
    private Integer calories; // 熱量 (kcal)
    private BigDecimal protein; // 蛋白質 (克, g)
    private BigDecimal fat; // 脂肪 (克, g)
    private BigDecimal carbohydrates; // 碳水化合物 (克, g)
    private BigDecimal sugar; // 糖 (克, g)
    private BigDecimal fiber; // 纖維 (克, g)
    private BigDecimal sodium; // 鈉含量 (毫克, mg)
    private BigDecimal vitaminC; // 維生素 C 含量 (毫克, mg)

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public BigDecimal getSugar() {
        return sugar;
    }

    public void setSugar(BigDecimal sugar) {
        this.sugar = sugar;
    }

    public BigDecimal getFiber() {
        return fiber;
    }

    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }

    public BigDecimal getSodium() {
        return sodium;
    }

    public void setSodium(BigDecimal sodium) {
        this.sodium = sodium;
    }

    public BigDecimal getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(BigDecimal vitaminC) {
        this.vitaminC = vitaminC;
    }
}
