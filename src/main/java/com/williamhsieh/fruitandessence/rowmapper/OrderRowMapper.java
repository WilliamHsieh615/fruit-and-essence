package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {

        Order order = new Order();
        order.setOrderId(resultSet.getInt("order_id"));
        order.setMemberId(resultSet.getInt("member_id"));
        order.setTotalAmount(resultSet.getInt("total_amount"));
        order.setShippingAddress(resultSet.getString("shipping_address"));
        order.setShippingPhone(resultSet.getString("shipping_phone"));
        order.setStatus(OrderStatus.valueOf(resultSet.getString("status")));
        order.setOrderDate(resultSet.getDate("order_date").toLocalDate());
        order.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        order.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return order;
    }
}
