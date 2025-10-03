package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.constant.ProductSize;
import com.williamhsieh.fruitandessence.model.ProductVariant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductVariantRowMapper implements RowMapper<ProductVariant> {

    @Override
    public ProductVariant mapRow(ResultSet resultSet, int i) throws SQLException {

        ProductVariant productVariant = new ProductVariant();
        productVariant.setProductVariantId(resultSet.getInt("product_variant_id"));
        productVariant.setProductId(resultSet.getInt("product_id"));
        productVariant.setProductSize(ProductSize.valueOf(resultSet.getString("product_size")));
        productVariant.setPrice(resultSet.getBigDecimal("price"));
        productVariant.setDiscountPrice(resultSet.getBigDecimal("discount_price"));
        productVariant.setUnit(resultSet.getString("unit"));
        productVariant.setSku(resultSet.getString("sku"));
        productVariant.setBarcode(resultSet.getString("barcode"));

        return productVariant;
    }
}
