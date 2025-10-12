package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.constant.DiscountType;
import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.*;
import com.williamhsieh.fruitandessence.rowmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countOrders(OrderQueryParams orderQueryParams) {

        String sql = "SELECT count(*) FROM orders WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;

    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        String sql = "SELECT order_id, order_number, member_id, subtotal, tax_amount, discountAmount, shipping_fee, total_amount, " +
                "shipping_phone, shipping_address, shipping_note " +
                "payment_methodId, shipping_methodId " +
                "order_status, shipping_date, tracking_number, cancel_reason, " +
                "created_date, last_modified_date " +
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

        String sql = "SELECT order_id, order_number, member_id, subtotal, tax_amount, discountAmount, shipping_fee, total_amount, " +
                "shipping_phone, shipping_address, shipping_note " +
                "payment_methodId, shipping_methodId " +
                "order_status, shipping_date, tracking_number, cancel_reason, " +
                "created_date, last_modified_date " +
                "FROM orders WHERE order_id = :orderId";

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
    public Map<Integer, List<OrderItem>> getOrderItemsByOrderIds(List<Integer> orderIds) {

        String sql = "SELECT order_item_id, order_id, product_id, " +
                "product_variant_id, quantity, price, item_total, notes " +
                "FROM order_item WHERE order_id IN (:orderIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("orderIds", orderIds);

        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        Map<Integer, List<OrderItem>> orderItemListMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));

        if (orderItemList.isEmpty()) {
            return  Map.of();
        }  else {
            return orderItemListMap;
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {

        String sql = "SELECT order_item_id, order_id, product_id, " +
                "product_variant_id, quantity, price, item_total, notes " +
                "FROM order_item WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        return orderItemList;
    }

    @Override
    public Map<Integer, List<OrderDiscount>> getOrderDiscountsByOrderIds(List<Integer> orderIds) {

        String sql = """
            SELECT odu.order_id, od.discount_id, od.member_id, od.discount_name, od.discount_code,
               od.discount_type, od.discount_value, od.discount_percentage,
               od.min_order_amount, od.total_usage_limit,
               od.start_date, od.end_date, od.created_date, od.last_modified_date
            FROM order_discount_usage odu
            JOIN order_discount od ON odu.discount_id = od.discount_id
            WHERE odu.order_id IN (:orderIds)
        """;

        Map<String, Object> map = new HashMap<>();
        map.put("orderIds", orderIds);

        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, map);

        Map<Integer, List<OrderDiscount>> orderDiscountListMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Integer orderId = (Integer) row.get("order_id");

            OrderDiscount orderDiscount = new OrderDiscount();
            orderDiscount.setDiscountId((Integer) row.get("discount_id"));
            orderDiscount.setMemberId((Integer) row.get("member_id"));
            orderDiscount.setDiscountName((String) row.get("discount_name"));
            orderDiscount.setDiscountCode((String) row.get("discount_code"));
            orderDiscount.setDiscountType(DiscountType.valueOf((String) row.get("discount_type")));
            orderDiscount.setDiscountValue((BigDecimal) row.get("discount_value"));
            orderDiscount.setDiscountPercentage((BigDecimal) row.get("discount_percentage"));
            orderDiscount.setMinOrderAmount((BigDecimal) row.get("min_order_amount"));
            orderDiscount.setTotalUsageLimit((Integer) row.get("total_usage_limit"));
            orderDiscount.setStartDate(((Timestamp) row.get("start_date")).toLocalDateTime());
            orderDiscount.setEndDate(((Timestamp) row.get("end_date")).toLocalDateTime());
            orderDiscount.setCreatedDate(((Timestamp) row.get("created_date")).toLocalDateTime());
            orderDiscount.setLastModifiedDate(((Timestamp) row.get("last_modified_date")).toLocalDateTime());

            orderDiscountListMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(orderDiscount);
        }

        return orderDiscountListMap;
    }

    @Override
    public List<OrderDiscount> getOrderDiscountsByOrderId(Integer orderId) {
        String sql = """
            SELECT od.discount_id, od.member_id, od.discount_name, od.discount_code,
               od.discount_type, od.discount_value, od.discount_percentage,
               od.min_order_amount, od.total_usage_limit,
               od.start_date, od.end_date, od.created_date, od.last_modified_date
            FROM order_discount_usage odu
            JOIN order_discount od ON odu.discount_id = od.discount_id
            WHERE odu.order_id = :orderId
        """;

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<OrderDiscount> orderDiscountList = namedParameterJdbcTemplate.query(sql, map, new OrderDiscountRowMapper());

        return orderDiscountList;
    }

    @Override
    public List<OrderDiscount> getOrderDiscountsByMemberId(Integer memberId) {

        String sql = "SELECT discount_id, member_id, discount_name, discount_code, " +
                "discount_type, discount_value, discount_percentage, " +
                "min_order_amount, total_usage_limit, " +
                "start_date, end_date, created_date, last_modified_date " +
                "FROM order_discount WHERE member_id = :memberId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);

        List<OrderDiscount> orderDiscountList = namedParameterJdbcTemplate.query(sql, map, new OrderDiscountRowMapper());

        return orderDiscountList;
    }

    @Override
    public OrderDiscount getOrderDiscountById(Integer discountId) {

        String sql = "SELECT discount_id, member_id, discount_name, discount_code, " +
                "discount_type, discount_value, discount_percentage, " +
                "min_order_amount, total_usage_limit, " +
                "start_date, end_date, created_date, last_modified_date " +
                "FROM order_discount WHERE discount_id = :discountId";

        Map<String, Object> map = new HashMap<>();
        map.put("discountId", discountId);

        OrderDiscount orderDiscount = namedParameterJdbcTemplate.queryForObject(sql, map, new OrderDiscountRowMapper());

        return orderDiscount;
    }

    @Override
    public List<Integer> getRoleIdsByDiscountId(Integer discountId) {
        String sql = "SELECT role_id FROM order_discount_role WHERE discount_id = :discountId";
        Map<String, Object> map = new HashMap<>();
        map.put("discountId", discountId);

        List<Integer> roleIdList = namedParameterJdbcTemplate.queryForList(sql, map, Integer.class);
        return roleIdList;
    }

    @Override
    public List<Integer> getProductIdsByDiscountId(Integer discountId) {
        String sql = "SELECT product_id FROM order_discount_product WHERE discount_id = :discountId";
        Map<String, Object> map = new HashMap<>();
        map.put("discountId", discountId);

        List<Integer> productIdList = namedParameterJdbcTemplate.queryForList(sql, map, Integer.class);
        return productIdList;
    }

    @Override
    public List<ProductCategory> getProductCategoriesByDiscountId(Integer discountId) {
        String sql = "SELECT product_category FROM order_discount_category WHERE discount_id = :discountId";
        Map<String, Object> map = new HashMap<>();
        map.put("discountId", discountId);

        List<ProductCategory> productCategoryList = namedParameterJdbcTemplate.query(sql, map, (resultSet, i) ->
                ProductCategory.valueOf(resultSet.getString("product_category")));

        return productCategoryList;
    }

    @Override
    public PaymentMethod getPaymentMethodById(Integer paymentMethodId) {
        String sql = "SELECT method_id, method_name, method_description " +
                "FROM payment_method WHERE method_id = :methodId";

        Map<String, Object> map = new HashMap<>();
        map.put("methodId", methodId);

        PaymentMethod paymentMethod = namedParameterJdbcTemplate.queryForObject(sql, map, new PaymentMethodRowMapper());

        return paymentMethod;
    }

    @Override
    public ShippingMethod getShippingMethodById(Integer shippingMethodId) {
        String sql = "SELECT method_id, method_name, provider_code, method_description " +
                "FROM shipping_method WHERE method_id = :methodId";

        Map<String, Object> map = new HashMap<>();
        map.put("methodId", methodId);

        ShippingMethod shippingMethod = namedParameterJdbcTemplate.queryForObject(sql, map, new ShippingMethodRowMapper());

        return shippingMethod;
    }

    @Override
    public Invoice getInvoiceByOrderId(Integer orderId) {
        String sql = "SELECT invoice_id, order_id, invoice_number, invoice_carrier, invoice_donation_code, company_tax_id, " +
                "issued, issued_date, created_date, last_modified_date " +
                "FROM invoice WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        Invoice invoice = namedParameterJdbcTemplate.queryForObject(sql, map, new InvoiceRowMapper());

        return invoice;
    }

    @Override
    public Integer createOrder(Integer memberId, Integer totalAmount, CreatedOrderRequest createdOrderRequest) {

        String sql = "INSERT INTO orders (order_number, member_id, subtotal, tax_amount, discount_amount, shipping_fee, total_amount, " +
                "shipping_phone, shipping_address, shipping_note " +
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
