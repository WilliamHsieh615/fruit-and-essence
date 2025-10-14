package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.ShippingMethod;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShippingMethodRowMapper implements RowMapper<ShippingMethod> {

    @Override
    public ShippingMethod mapRow(ResultSet resultSet, int i) throws SQLException {

        ShippingMethod shippingMethod = new ShippingMethod();
        shippingMethod.setShippingMethodId(resultSet.getInt("shipping_method_id"));
        shippingMethod.setShippingMethodName(resultSet.getString("shipping_method_name"));
        shippingMethod.setProviderCode(resultSet.getString("provider_code"));
        shippingMethod.setDescription(resultSet.getString("description"));

        return shippingMethod;
    }
}
