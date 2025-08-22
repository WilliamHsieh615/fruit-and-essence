package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer createOrder(Integer memberId, Integer totalAmount, CreatedOrderRequest createdOrderRequest) {

        String sql = "INSERT INTO orders (member_id, total_amount, shipping_phone, shipping_address, " +
                "status, order_date, created_date, last_modified_date)" +
                "VALUES (:memberId, :totalAmount, :shippingPhone, :shippingAddress, " +
                ":status, :orderDate, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("totalAmount", totalAmount);
        map.put("shippingPhone", createdOrderRequest.getShippingPhone());
        map.put("shippingAddress", createdOrderRequest.getShippingAddress());
        map.put("status", OrderStatus.PENDING.toString()); // 訂單狀態不透過前端傳入，而是由後端設定

        LocalDateTime now = LocalDateTime.now();
        map.put("orderDate", now);
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int orderId = keyHolder.getKey().intValue();

        return orderId;
    }

    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {

        // 第一種方法：使用 for loop 加入數據
//        for (OrderItem orderItem : orderItemList) {
//
//            String sql = "INSTER INTO order_item(order_id, product_id, " +
//                         "purchased_count, purchase_weight, amount) " +
//                         "VALUES (:orderId, :productId, :purchaseCount, :purchaseWeight, :amount)";
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("orderId", orderId);
//            map.put("productId", orderItem.getProductId());
//            map.put("purchasedCount", orderItem.getPurchasedCount());
//            map.put("purchaseWeight", orderItem.getPurchasedWeight());
//            map.put("amount", orderItem.getAmount());
//
//            namedParameterJdbcTemplate.update(sql, map);
//        }

        // 第二種方法：使用 batchUpdate 加入數據
        String sql = "INSERT INTO order_item(order_id, product_id, " +
                     "purchased_count, purchased_weight, amount) " +
                     "VALUES (:orderId, :productId, :purchasedCount, :purchasedWeight, :amount)";

        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[orderItemList.size()];

        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);

            parameterSources[i] = new MapSqlParameterSource();
            parameterSources[i].addValue("orderId", orderId);
            parameterSources[i].addValue("productId", orderItem.getProductId());
            parameterSources[i].addValue("purchasedCount", orderItem.getPurchasedCount());
            parameterSources[i].addValue("purchasedWeight", orderItem.getPurchasedWeight());
            parameterSources[i].addValue("amount", orderItem.getAmount());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);

    }
}
