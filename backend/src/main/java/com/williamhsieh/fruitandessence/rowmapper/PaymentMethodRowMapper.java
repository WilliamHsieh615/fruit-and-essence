package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.PaymentMethod;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMethodRowMapper implements RowMapper<PaymentMethod> {

    @Override
    public PaymentMethod mapRow(ResultSet resultSet, int i) throws SQLException {

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(resultSet.getInt("payment_method_id"));
        paymentMethod.setPaymentMethodName(resultSet.getString("payment_method_name"));
        paymentMethod.setDescription(resultSet.getString("description"));

        return paymentMethod;
    }
}
