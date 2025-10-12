package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.*;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    Integer countOrders(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Map<Integer, List<OrderItem>> getOrderItemsByOrderIds(List<Integer> orderIds);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    Map<Integer, List<OrderDiscount>> getOrderDiscountsByOrderIds(List<Integer> orderIds);

    List<OrderDiscount> getOrderDiscountsByOrderId(Integer orderId);

    List<OrderDiscount> getOrderDiscountsByMemberId(Integer memberId);

    OrderDiscount getOrderDiscountById(Integer discountId);

    List<Integer> getRoleIdsByDiscountId(Integer discountId);

    List<Integer> getProductIdsByDiscountId(Integer discountId);

    List<ProductCategory> getProductCategoriesByDiscountId(Integer discountId);

    PaymentMethod getPaymentMethodById(Integer paymentMethodId);

    ShippingMethod getShippingMethodById(Integer shippingMethodId);

    Invoice getInvoiceByOrderId(Integer orderId);

    Integer createOrder(Integer memberId, Integer totalAmount, CreatedOrderRequest createdOrderRequest);

    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

    Integer createOrderDiscount(OrderDiscount orderDiscount);

}
