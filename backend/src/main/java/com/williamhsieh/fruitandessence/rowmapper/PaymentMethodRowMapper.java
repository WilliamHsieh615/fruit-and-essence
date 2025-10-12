package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.PaymentMethod;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMethodRowMapper implements RowMapper<PaymentMethod> {

    @Override
    public PaymentMethod mapRow(ResultSet resultSet, int i) throws SQLException {

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodId(resultSet.getInt("method_id"));
        paymentMethod.setMethodName(resultSet.getString("method_name"));
        paymentMethod.setMethodDescription(resultSet.getString("description"));

        return paymentMethod;
    }
}
