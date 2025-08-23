package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItemResponse> {

    @Override
    public OrderItemResponse mapRow(ResultSet resultSet, int i) throws SQLException {

        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setOrderItemId(resultSet.getInt("order_item_id"));
        orderItemResponse.setOrderId(resultSet.getInt("order_id"));
        orderItemResponse.setProductId(resultSet.getInt("product_id"));
        orderItemResponse.setPurchasedCount(resultSet.getInt("purchased_count"));
        orderItemResponse.setPurchasedWeight(resultSet.getDouble("purchased_weight"));
        orderItemResponse.setAmount(resultSet.getInt("amount"));

        orderItemResponse.setProductName(resultSet.getString("product_name"));
        orderItemResponse.setImageUrl(resultSet.getString("image_url"));
        orderItemResponse.setPricePerUnit(resultSet.getInt("price_per_unit"));
        orderItemResponse.setUnit(resultSet.getString("unit"));

        return orderItemResponse;
    }
}
