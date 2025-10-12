package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.constant.DiscountType;
import com.williamhsieh.fruitandessence.model.OrderDiscount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDiscountRowMapper implements RowMapper<OrderDiscount> {

    @Override
    public OrderDiscount mapRow(ResultSet resultSet, int i) throws SQLException {

        OrderDiscount orderDiscount = new OrderDiscount();
        orderDiscount.setDiscountId(resultSet.getInt("discount_id"));
        orderDiscount.setMemberId(resultSet.getInt("member_id"));
        orderDiscount.setDiscountName(resultSet.getString("discount_name"));
        orderDiscount.setDiscountCode(resultSet.getString("discount_code"));

        orderDiscount.setDiscountType(DiscountType.valueOf(resultSet.getString("discount_type")));
        orderDiscount.setDiscountValue(resultSet.getBigDecimal("discount_value"));
        orderDiscount.setDiscountPercentage(resultSet.getBigDecimal("discount_percentage"));

        orderDiscount.setMinOrderAmount(resultSet.getBigDecimal("min_order_amount"));
        orderDiscount.setTotalUsageLimit(resultSet.getInt("total_usage_limit"));

        orderDiscount.setStartDate(resultSet.getTimestamp("start_date").toLocalDateTime());
        orderDiscount.setEndDate(resultSet.getTimestamp("end_date").toLocalDateTime());

        orderDiscount.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        orderDiscount.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return orderDiscount;
    }
}
