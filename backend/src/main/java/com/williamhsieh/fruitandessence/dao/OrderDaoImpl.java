package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.constant.DiscountType;
import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.dto.AdminOrderQueryParams;
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

        sql += " AND member_id = :memberId";
        map.put("memberId", orderQueryParams.getMemberId());

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;

    }

    @Override
    public Integer countAllOrders(AdminOrderQueryParams adminOrderQueryParams) {

        String sql = "SELECT count(*) FROM orders WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        sql += " AND order_status = :orderStatus";
        map.put("orderStatus", adminOrderQueryParams.getOrderStatus().name());


        sql += " AND created_date >= :startDate";
        map.put("startDate", adminOrderQueryParams.getStartDate());

        sql += " AND created_date <= :endDate";
        map.put("endDate", adminOrderQueryParams.getEndDate());

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        String sql = "SELECT order_id, order_number, member_id, " +
                "subtotal, tax_amount, discount_amount, shipping_fee, total_amount, " +
                "shipping_phone, shipping_address, shipping_note, " +
                "payment_method_id, shipping_method_id, " +
                "order_status, shipping_date, tracking_number, cancel_reason, " +
                "created_date, last_modified_date " +
                "FROM orders WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql += " AND member_id = :memberId";
        map.put("memberId", orderQueryParams.getMemberId());

        // 排序
        sql += " ORDER BY created_date DESC";

        // 分頁
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;
    }

    @Override
    public List<Order> getAllOrders(AdminOrderQueryParams adminOrderQueryParams) {

        String sql = "SELECT order_id, order_number, member_id, " +
                "subtotal, tax_amount, discount_amount, shipping_fee, total_amount, " +
                "shipping_phone, shipping_address, shipping_note, " +
                "payment_method_id, shipping_method_id, " +
                "order_status, shipping_date, tracking_number, cancel_reason, " +
                "created_date, last_modified_date " +
                "FROM orders WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql += " AND order_status = :orderStatus";
        map.put("orderStatus", adminOrderQueryParams.getOrderStatus().name());

        sql += " AND created_date >= :startDate";
        map.put("startDate", adminOrderQueryParams.getStartDate());

        sql += " AND created_date <= :endDate";
        map.put("endDate", adminOrderQueryParams.getEndDate());

        // 排序
        sql += " ORDER BY created_date DESC";

        // 分頁
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", adminOrderQueryParams.getLimit());
        map.put("offset", adminOrderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {

        String sql = "SELECT order_id, order_number, member_id, " +
                "subtotal, tax_amount, discountAmount, shipping_fee, total_amount, " +
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
            return Map.of();
        } else {
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
                    SELECT odu.order_id, od.discount_id, od.discount_name, od.discount_code,
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
    public OrderDiscount getOrderDiscountByDiscountCode(String discountCode) {

        String sql = "SELECT discount_id, member_id, discount_name, discount_code, " +
                "discount_type, discount_value, discount_percentage, " +
                "min_order_amount, total_usage_limit, " +
                "start_date, end_date, created_date, last_modified_date " +
                "FROM order_discount WHERE discount_code = :discountCode";

        Map<String, Object> map = new HashMap<>();
        map.put("discountCode", discountCode);

        OrderDiscount orderDiscount = namedParameterJdbcTemplate.queryForObject(sql, map, new OrderDiscountRowMapper());

        return orderDiscount;
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
    public List<PaymentMethod> getPaymentMethods() {

        String sql = "SELECT payment_method_id, payment_method_name, description FROM payment_method";

        List<PaymentMethod> paymentMethodList = namedParameterJdbcTemplate.query(sql, new PaymentMethodRowMapper());

        return paymentMethodList;
    }

    @Override
    public List<ShippingMethod> getShippingMethods() {

        String sql = "SELECT shipping_method_id, shipping_method_name, provider_code, description FROM shipping_method";

        List<ShippingMethod> shippingMethodList = namedParameterJdbcTemplate.query(sql, new ShippingMethodRowMapper());

        return shippingMethodList;
    }

    @Override
    public List<OrderDiscount> getOrderDiscounts() {

        String sql = "SELECT discount_id, discount_name, discount_code, " +
                "discount_code, discount_type, discount_value, discount_percentage, min_order_amount, total_usage_limit, " +
                "start_date, end_date, created_date, last_modified_date " +
                "FROM order_discount";

        List<OrderDiscount> orderDiscountList = namedParameterJdbcTemplate.query(sql, new OrderDiscountRowMapper());

        return orderDiscountList;
    }

    @Override
    public Map<Integer, PaymentMethod> getPaymentMethodsByIds(List<Integer> paymentMethodIds) {

        String sql = "SELECT payment_method_id, payment_method_name, description " +
                "FROM payment_method WHERE payment_method_id IN (:paymentMethodIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("paymentMethodIds", paymentMethodIds);

        List<PaymentMethod> paymentMethodList = namedParameterJdbcTemplate.query(sql, map, new PaymentMethodRowMapper());

        Map<Integer, PaymentMethod> paymentMethodMap = paymentMethodList.stream().collect(Collectors.toMap(PaymentMethod::getPaymentMethodId, paymentMethod -> paymentMethod));

        if (paymentMethodList.isEmpty()) {
            return Map.of();
        } else {
            return paymentMethodMap;
        }
    }

    @Override
    public Map<Integer, ShippingMethod> getShippingMethodsByIds(List<Integer> shippingMethodIds) {

        String sql = "SELECT shipping_method_id, shipping_method_name, provider_code, method_description " +
                "FROM shipping_method WHERE shipping_method_id IN (:shippingMethodIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("shippingMethodIds", shippingMethodIds);

        List<ShippingMethod> shippingMethodList = namedParameterJdbcTemplate.query(sql, map, new ShippingMethodRowMapper());

        Map<Integer, ShippingMethod> shippingMethodMap = shippingMethodList.stream().collect(Collectors.toMap(ShippingMethod::getShippingMethodId, shippingMethod -> shippingMethod));

        if (shippingMethodList.isEmpty()) {
            return Map.of();
        } else {
            return shippingMethodMap;
        }
    }

    @Override
    public PaymentMethod getPaymentMethodById(Integer paymentMethodId) {
        String sql = "SELECT payment_method_id, payment_method_name, description " +
                "FROM payment_method WHERE payment_method_id = :paymentMethodId";

        Map<String, Object> map = new HashMap<>();
        map.put("paymentMethodId", paymentMethodId);

        PaymentMethod paymentMethod = namedParameterJdbcTemplate.queryForObject(sql, map, new PaymentMethodRowMapper());

        return paymentMethod;
    }

    @Override
    public ShippingMethod getShippingMethodById(Integer shippingMethodId) {
        String sql = "SELECT shipping_method_id, shipping_method_name, provider_code, method_description " +
                "FROM shipping_method WHERE shipping_method_id = :shippingMethodId";

        Map<String, Object> map = new HashMap<>();
        map.put("shippingMethodId", shippingMethodId);

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
    public Map<Integer, Invoice> getInvoicesByOrderIds(List<Integer> orderIds) {

        String sql = "SELECT invoice_id, order_id, invoice_number, invoice_carrier, invoice_donation_code, company_tax_id, " +
                "issued, issued_date, created_date, last_modified_date " +
                "FROM invoice WHERE order_id IN (:orderIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("orderIds", orderIds);

        List<Invoice> invoiceList = namedParameterJdbcTemplate.query(sql, map, new InvoiceRowMapper());

        Map<Integer, Invoice> invoiceMap = invoiceList.stream().collect(Collectors.toMap(Invoice::getOrderId, invoice -> invoice));

        if (invoiceList.isEmpty()) {
            return Map.of();
        } else {
            return invoiceMap;
        }
    }

    @Override
    public Integer createOrder(Order order) {

        String sql = "INSERT INTO orders (order_number, member_id, " +
                "subtotal, tax_amount, discount_amount, shipping_fee, total_amount, " +
                "shipping_phone, shipping_address, shipping_note, " +
                "payment_method_id, shipping_method_id, " +
                "order_status, shipping_date, tracking_number, cancel_reason, " +
                "created_date, last_modified_date)" +
                "VALUES (:orderNumber, :memberId, " +
                ":subtotal, :taxAmount, :discountAmount, :shippingFee, :totalAmount, " +
                ":shippingPhone, :shippingAddress, :shippingNote, " +
                ":paymentMethodId, :shippingMethodId, " +
                ":orderStatus, :shippingDate, :trackingNumber, :cancelReason, " +
                ":createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();

        map.put("orderNumber", order.getOrderNumber());
        map.put("memberId", order.getMemberId());
        map.put("subtotal", order.getSubtotal());
        map.put("taxAmount", order.getTaxAmount());
        map.put("discountAmount", order.getDiscountAmount());
        map.put("shippingFee", order.getShippingFee());
        map.put("totalAmount", order.getTotalAmount());

        map.put("shippingPhone", order.getShippingPhone());
        map.put("shippingAddress", order.getShippingAddress());
        map.put("shippingNote", order.getShippingNote());

        map.put("paymentMethodId", order.getPaymentMethodId());
        map.put("shippingMethodId", order.getShippingMethodId());

        map.put("orderStatus", order.getOrderStatus());
        map.put("shippingDate", order.getShippingDate());
        map.put("trackingNumber", order.getTrackingNumber());
        map.put("cancelReason", order.getCancelReason());

        map.put("createdDate", order.getCreatedDate());
        map.put("lastModifiedDate", order.getLastModifiedDate());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int orderId = keyHolder.getKey().intValue();

        return orderId;
    }

    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {

        String sql = "INSERT INTO order_item(order_id, product_id, product_variant_id, " +
                "quantity, price, item_total, notes) " +
                "VALUES (:orderId, :productId, :productVariantId, " +
                ":quantity, :price, :itemTotal, :notes)";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("productId", orderItem.getProductId());
            map.put("productVariantId", orderItem.getProductVariantId());
            map.put("quantity", orderItem.getQuantity());
            map.put("price", orderItem.getPrice());
            map.put("itemTotal", orderItem.getItemTotal());
            map.put("notes", orderItem.getNotes());

            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public Integer createOrderDiscount(OrderDiscount orderDiscount) {

        String sql = "INSERT INTO order_discount(discount_name, discount_code, " +
                "discount_type, discount_value, discount_percentage, " +
                "min_order_amount, total_usage_limit, start_date, end_date, " +
                "created_date, last_modified_date) " +
                "VALUES (:memberId, :discountName, :discountCode, " +
                ":discountType, :discountValue, :discountPercentage, " +
                ":minOrderAmount, :totalUsageLimit, :startDate, :endDate, " +
                ":createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("discountName", orderDiscount.getDiscountName());
        map.put("discountCode", orderDiscount.getDiscountCode());
        map.put("discountType", orderDiscount.getDiscountType());
        map.put("discountValue", orderDiscount.getDiscountValue());
        map.put("discountPercentage", orderDiscount.getDiscountPercentage());
        map.put("minOrderAmount", orderDiscount.getMinOrderAmount());
        map.put("totalUsageLimit", orderDiscount.getTotalUsageLimit());
        map.put("startDate", orderDiscount.getStartDate());
        map.put("endDate", orderDiscount.getEndDate());
        map.put("createdDate", orderDiscount.getCreatedDate());
        map.put("lastModifiedDate", orderDiscount.getLastModifiedDate());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int discountId = keyHolder.getKey().intValue();

        return discountId;
    }

    @Override
    public void createOrderDiscountRoles(Integer discountId, List<Integer> roleIdList) {

        String sql = "INSERT INTO order_discount_role (discount_id, role_id) " +
                "VALUES (:discountId, :roleId)";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (Integer roleId : roleIdList) {
            Map<String, Object> map = new HashMap<>();
            map.put("discountId", discountId);
            map.put("roleId", roleId);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public void createOrderDiscountProducts(Integer discountId, List<Integer> productIdList) {

        String sql = "INSERT INTO order_discount_product (discount_id, product_id) " +
                "VALUES (:discountId, :productId)";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (Integer productId : productIdList) {
            Map<String, Object> map = new HashMap<>();
            map.put("discountId", discountId);
            map.put("productId", productId);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public void createOrderDiscountProductCategories(Integer discountId, List<ProductCategory> productCategoryList) {

        String sql = "INSERT INTO order_discount_category (discount_id, product_category) " +
                "VALUES (:discountId, :productCategory)";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            Map<String, Object> map = new HashMap<>();
            map.put("discountId", discountId);
            map.put("productCategory", productCategory);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public void addOrderDiscountForMemberId(Integer discountId, Integer memberId) {

        String sql = "INSERT INTO member_has_order_discount (member_id, discount_id) VALUES (:memberId, :discountId)";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("discountId", discountId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void removeOrderDiscountForMemberId(Integer discountId, Integer memberId) {

        String sql = "DELETE FROM member_has_order_discount WHERE member_id = :memberId AND discount_id = :discountId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("discountId", discountId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public Integer createOrderDiscountUsage(OrderDiscountUsage orderDiscountUsage) {

        String sql = "INSERT INTO order_discount_usage (discount_id, member_id, used_at, order_id) " +
                "VALUES (:discountId, :memberId, :usedAt, :orderId)";

        Map<String, Object> map = new HashMap<>();
        map.put("discountId", orderDiscountUsage.getDiscountId());
        map.put("memberId", orderDiscountUsage.getMemberId());
        map.put("usedAt", orderDiscountUsage.getUsedAt());
        map.put("orderId", orderDiscountUsage.getOrderId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int discountId = keyHolder.getKey().intValue();

        return discountId;
    }

    @Override
    public List<Integer> createOrderDiscountUsages(List<OrderDiscountUsage> orderDiscountUsages) {

        String sql = "INSERT INTO order_discount_usage (discount_id, member_id, used_at, order_id) " +
                "VALUES (:discountId, :memberId, :usedAt, :orderId)";

        List<Integer> usageIdList = new ArrayList<>();
        for (OrderDiscountUsage orderDiscountUsage : orderDiscountUsages) {
            Map<String, Object> map = new HashMap<>();
            map.put("discountId", orderDiscountUsage.getDiscountId());
            map.put("memberId", orderDiscountUsage.getMemberId());
            map.put("usedAt", orderDiscountUsage.getUsedAt());
            map.put("orderId", orderDiscountUsage.getOrderId());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
            usageIdList.add(keyHolder.getKey().intValue());
        }

        return usageIdList;
    }

    @Override
    public Integer createPaymentMethod(PaymentMethod paymentMethod) {

        String sql = "INSERT INTO payment_method (method_name, description) " +
                "VALUES (:methodName, :description)";

        Map<String, Object> map = new HashMap<>();
        map.put("methodName", paymentMethod.getPaymentMethodName());
        map.put("description", paymentMethod.getDescription());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int paymentMethodId = keyHolder.getKey().intValue();

        return paymentMethodId;
    }

    @Override
    public Integer createShippingMethod(ShippingMethod shippingMethod) {

        String sql = "INSERT INTO shipping_method (shipping_method_name, provider_code, description) " +
                "VALUES (:shippingMethodName, :providerCode, :description)";

        Map<String, Object> map = new HashMap<>();
        map.put("shippingMethodName", shippingMethod.getShippingMethodName());
        map.put("providerCode", shippingMethod.getProviderCode());
        map.put("description", shippingMethod.getDescription());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int shippingMethodId = keyHolder.getKey().intValue();

        return shippingMethodId;
    }

    @Override
    public void createInvoice(Invoice invoice) {

        String sql = "INSERT INTO invoice (order_id, invoice_number, " +
                "invoice_carrier, invoice_donation_code, company_tax_id, " +
                "issued, issued_date, created_date, last_modified_date) " +
                "VALUES (:orderId, :invoiceNumber, " +
                ":invoiceCarrier, :invoiceDonationCode, :companyTaxId, " +
                ":issued, :issuedDate, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", invoice.getOrderId());
        map.put("invoiceNumber", invoice.getInvoiceNumber());
        map.put("invoiceCarrier", invoice.getInvoiceCarrier());
        map.put("invoiceDonationCode", invoice.getInvoiceDonationCode());
        map.put("companyTaxId", invoice.getCompanyTaxId());
        map.put("issued", invoice.getIssued());
        map.put("issuedDate", invoice.getIssuedDate());
        map.put("createdDate", invoice.getCreatedDate());
        map.put("lastModifiedDate", invoice.getLastModifiedDate());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void updateOrder(Order order) {

        String sql = "UPDATE orders " +
                "SET order_number = :orderNumber, member_id = :memberId, " +
                "subtotal = :subtotal, tax_amount = :taxAmount, discount_amount = :discountAmount, " +
                "shipping_fee = :shippingFee, total_amount = :totalAmount, " +
                "shipping_phone = :shippingPhone, shipping_address = :shippingAddress, shipping_note = :shippingNote, " +
                "payment_method_id = :paymentMethodId, shipping_method_id = :shippingMethodId, " +
                "order_status = :orderStatus, shipping_date = :shippingDate, tracking_number = :trackingNumber, " +
                "cancel_reason = :cancelReason, last_modified_date = :lastModifiedDate " +
                "WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();

        map.put("orderId", order.getOrderId());
        map.put("orderNumber", order.getOrderNumber());
        map.put("memberId", order.getMemberId());
        map.put("subtotal", order.getSubtotal());
        map.put("taxAmount", order.getTaxAmount());
        map.put("discountAmount", order.getDiscountAmount());
        map.put("shippingFee", order.getShippingFee());
        map.put("totalAmount", order.getTotalAmount());
        map.put("shippingPhone", order.getShippingPhone());
        map.put("shippingAddress", order.getShippingAddress());
        map.put("shippingNote", order.getShippingNote());
        map.put("paymentMethodId", order.getPaymentMethodId());
        map.put("shippingMethodId", order.getShippingMethodId());
        map.put("orderStatus", order.getOrderStatus());
        map.put("shippingDate", order.getShippingDate());
        map.put("trackingNumber", order.getTrackingNumber());
        map.put("cancelReason", order.getCancelReason());
        map.put("lastModifiedDate", order.getLastModifiedDate());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public Order updateOrderStatus(Order order) {

        String sql = "UPDATE orders SET order_status = :orderStatus, last_modified_date = :lastModifiedDate " +
                "WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderStatus", order.getOrderStatus());
        map.put("lastModifiedDate", order.getLastModifiedDate());
        map.put("orderId", order.getOrderId());

        namedParameterJdbcTemplate.update(sql, map);

        return getOrderById(order.getOrderId());
    }

    @Override
    public void updateInvoice(Invoice invoice) {

        String sql = "UPDATE invoice SET order_id = :orderId, invoice_number = :invoiceNumber, " +
                "invoice_carrier = :invoiceCarrier, invoice_donation_code = :invoiceDonationCode, company_tax_id = :companyTaxId, " +
                "issued = :issued, issued_date = :issuedDate, last_modified_date = :lastModifiedDate " +
                "WHERE invoice_id = :invoiceId";

        Map<String, Object> map = new HashMap<>();
        map.put("invoiceId", invoice.getInvoiceId());
        map.put("orderId", invoice.getOrderId());
        map.put("invoiceNumber", invoice.getInvoiceNumber());
        map.put("invoiceCarrier", invoice.getInvoiceCarrier());
        map.put("invoiceDonationCode", invoice.getInvoiceDonationCode());
        map.put("companyTaxId", invoice.getCompanyTaxId());
        map.put("issued", invoice.getIssued());
        map.put("issuedDate", invoice.getIssuedDate());
        map.put("lastModifiedDate", invoice.getLastModifiedDate());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public ShippingMethod updateShippingMethod(ShippingMethod shippingMethod) {

        String sql = "UPDATE shipping_method " +
                "SET shipping_method_name = :shippingMethodName, provider_code = :providerCode, description = :description " +
                "WHERE shipping_method_id = :shippingMethodId";

        Map<String, Object> map = new HashMap<>();
        map.put("shippingMethodName", shippingMethod.getShippingMethodName());
        map.put("providerCode", shippingMethod.getProviderCode());
        map.put("description", shippingMethod.getDescription());
        map.put("shippingMethodId", shippingMethod.getShippingMethodId());

        namedParameterJdbcTemplate.update(sql, map);

        return getShippingMethodById(shippingMethod.getShippingMethodId());
    }

    @Override
    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        String sql = "UPDATE payment_method " +
                "SET payment_method_name = :paymentMethodName, description = :description " +
                "WHERE payment_method_id = :paymentMethodId";

        Map<String, Object> map = new HashMap<>();
        map.put("paymentMethodName", paymentMethod.getPaymentMethodName());
        map.put("description", paymentMethod.getDescription());
        map.put("paymentMethodId", paymentMethod.getPaymentMethodId());

        namedParameterJdbcTemplate.update(sql, map);

        return getPaymentMethodById(paymentMethod.getPaymentMethodId());
    }

    @Override
    public OrderDiscount updateOrderDiscount(OrderDiscount orderDiscount) {
        String sql = "UPDATE order_discount SET " +
                "discount_name = :discountName, discount_code = :discountCode, " +
                "discount_type = :discountType, discount_value = :discountValue, discount_percentage = :discountPercentage, " +
                "min_order_amount = :minOrderAmount, total_usage_limit = :totalUsageLimit, " +
                "start_date = :startDate, end_date = :endDate, " +
                "last_modified_date = :lastModified " +
                "WHERE discount_id = :discountId";

        Map<String, Object> map = new HashMap<>();
        map.put("discountName", orderDiscount.getDiscountName());
        map.put("discountCode", orderDiscount.getDiscountCode());
        map.put("discountType", orderDiscount.getDiscountType());
        map.put("discountValue", orderDiscount.getDiscountValue());
        map.put("discountPercentage", orderDiscount.getDiscountPercentage());
        map.put("minOrderAmount", orderDiscount.getMinOrderAmount());
        map.put("totalUsageLimit", orderDiscount.getTotalUsageLimit());
        map.put("startDate", orderDiscount.getStartDate());
        map.put("endDate", orderDiscount.getEndDate());
        map.put("lastModified", orderDiscount.getLastModifiedDate());
        map.put("discountId", orderDiscount.getDiscountId());

        int updated = namedParameterJdbcTemplate.update(sql, map);

        return getOrderDiscountById(orderDiscount.getDiscountId());
    }
}
