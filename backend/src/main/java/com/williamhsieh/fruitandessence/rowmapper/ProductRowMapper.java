package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {

        Product product = new Product();
        product.setProductId(resultSet.getInt("product_id"));
        product.setProductName(resultSet.getString("product_name"));
        product.setCategory(ProductCategory.valueOf(resultSet.getString("category")));
        product.setImageUrl(resultSet.getString("image_url"));

        product.setStock(resultSet.getDouble("stock"));
        product.setPricePerUnit(resultSet.getInt("price_per_unit"));
        product.setUnit(resultSet.getString("unit")); // 如果資料庫有存，否則用預設 "kg"
        product.setUnitType(ProductUnitType.valueOf(resultSet.getString("unit_type")));
        product.setCount(resultSet.getInt("count"));
        product.setWeight(resultSet.getDouble("weight")); // 如果資料庫有存，否則用預設 0.0

        product.setDescription(resultSet.getString("description"));
        product.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        product.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());


        return product;
    }
}
