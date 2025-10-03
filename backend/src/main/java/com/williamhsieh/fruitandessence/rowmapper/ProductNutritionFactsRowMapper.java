package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.ProductNutritionFacts;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductNutritionFactsRowMapper implements RowMapper<ProductNutritionFacts> {

    @Override
    public ProductNutritionFacts mapRow(ResultSet resultSet, int i) throws SQLException {

        ProductNutritionFacts productNutritionFacts = new ProductNutritionFacts();
        productNutritionFacts.setProductNutritionId(resultSet.getInt("product_nutrition_id"));
        productNutritionFacts.setProductId(resultSet.getInt("product_id"));
        productNutritionFacts.setServingSize(resultSet.getString("serving_size"));
        productNutritionFacts.setCalories(resultSet.getInt("calories"));
        productNutritionFacts.setProtein(resultSet.getBigDecimal("protein"));
        productNutritionFacts.setFat(resultSet.getBigDecimal("fat"));
        productNutritionFacts.setCarbohydrates(resultSet.getBigDecimal("carbohydrates"));
        productNutritionFacts.setSugar(resultSet.getBigDecimal("sugar"));
        productNutritionFacts.setFiber(resultSet.getBigDecimal("fiber"));
        productNutritionFacts.setSodium(resultSet.getBigDecimal("sodium"));
        productNutritionFacts.setVitaminC(resultSet.getBigDecimal("vitaminC"));

        return productNutritionFacts;
    }
}
