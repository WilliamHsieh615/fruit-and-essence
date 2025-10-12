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
        order.setOrderNumber(resultSet.getString("order_number"));
        order.setMemberId(resultSet.getInt("member_id"));
        order.setSubtotal(resultSet.getBigDecimal("subtotal"));
        order.setTaxAmount(resultSet.getBigDecimal("tax_amount"));
        order.setDiscountAmount(resultSet.getBigDecimal("discount_amount"));
        order.setShippingFee(resultSet.getBigDecimal("shipping_fee"));
        order.setTotalAmount(resultSet.getBigDecimal("total_amount"));

        order.setShippingPhone(resultSet.getString("shipping_phone"));
        order.setShippingAddress(resultSet.getString("shipping_address"));
        order.setShippingNote(resultSet.getString("shipping_note"));

        order.setOrderStatus(OrderStatus.valueOf(resultSet.getString("order_status")));
        order.setShippingDate(resultSet.getTimestamp("shipping_date").toLocalDateTime());
        order.setTrackingNumber(resultSet.getString("tracking_number"));
        order.setCancelReason(resultSet.getString("cancel_reason"));

        order.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        order.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return order;
    }
}
