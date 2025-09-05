package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderItem;
import com.williamhsieh.fruitandessence.rowmapper.OrderItemRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.OrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {

        String sql = "SELECT count(*) FROM orders WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;

    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        String sql = "SELECT order_id, member_id, total_amount, shipping_phone, shipping_address, " +
                "status, order_date, created_date, last_modified_date " +
                "FROM orders WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);

        // 排序
        sql = sql + " ORDER BY created_date DESC";

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {

        String sql = "SELECT order_id, member_id, total_amount, shipping_phone, shipping_address, " +
                "status, order_date, created_date, last_modified_date " +
                "FROM orders WHERE order_Id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        if (orderList.isEmpty()) {
            return null;
        } else {
            return orderList.get(0);
        }

    }

    @Override
    public List<OrderItemResponse> getOrderItemsByOrderId(Integer orderId) {

        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, " +
                     "oi.purchased_count, oi.purchased_weight, oi.amount, " +
                     "p.product_name, p.image_url, p.price_per_unit, p.unit " +
                     "FROM order_item as oi " +
                     "LEFT JOIN product as p ON oi.product_id = p.product_id " +
                     "WHERE oi.order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<OrderItemResponse> orderItemListResponse = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        return orderItemListResponse;
    }

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

        LocalDate orderDate = LocalDate.now().plusDays(2); // 兩天後
        map.put("orderDate", orderDate);
        LocalDateTime now = LocalDateTime.now();
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
//            String sql = "INSERT INTO order_item(order_id, product_id, " +
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

    private String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams) {
        if(orderQueryParams.getMemberId() != null) {
            sql = sql + " AND member_id = :memberId";
            map.put("memberId", orderQueryParams.getMemberId());
        }

        return sql;
    }

}
