package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {

        Product product = new Product();
        product.setProductId(resultSet.getInt("product_id"));
        product.setProductName(resultSet.getString("product_name"));
        product.setProductCategory(ProductCategory.valueOf(resultSet.getString("product_category")));
        product.setProductDescription(resultSet.getString("product_description"));
        product.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        product.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return product;
    }
}
