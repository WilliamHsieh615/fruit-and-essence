package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.constant.ProductCategory;
import com.williamhsieh.fruitandessence.dto.AdminOrderQueryParams;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.*;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    Integer countOrders(OrderQueryParams orderQueryParams);

    Integer countAllOrders(AdminOrderQueryParams adminOrderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    List<Order> getAllOrders(AdminOrderQueryParams adminOrderQueryParams);

    Order getOrderById(Integer orderId);

    Map<Integer, List<OrderItem>> getOrderItemsByOrderIds(List<Integer> orderIds);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    Map<Integer, List<OrderDiscount>> getOrderDiscountsByOrderIds(List<Integer> orderIds);

    List<OrderDiscount> getOrderDiscountsByOrderId(Integer orderId);

    List<OrderDiscount> getOrderDiscountsByMemberId(Integer memberId);

    OrderDiscount getOrderDiscountByDiscountCode(String discountCode);

    OrderDiscount getOrderDiscountById(Integer discountId);

    List<Integer> getRoleIdsByDiscountId(Integer discountId);

    List<Integer> getProductIdsByDiscountId(Integer discountId);

    List<ProductCategory> getProductCategoriesByDiscountId(Integer discountId);

    List<PaymentMethod> getPaymentMethods();

    List<ShippingMethod> getShippingMethods();

    List<OrderDiscount> getOrderDiscounts();

    Map<Integer, PaymentMethod> getPaymentMethodsByIds(List<Integer> paymentMethodIds);

    Map<Integer, ShippingMethod> getShippingMethodsByIds(List<Integer> shippingMethodIds);

    PaymentMethod getPaymentMethodById(Integer paymentMethodId);

    ShippingMethod getShippingMethodById(Integer shippingMethodId);

    Invoice getInvoiceByOrderId(Integer orderId);

    Map<Integer, Invoice> getInvoicesByOrderIds(List<Integer> orderIds);

    Integer createOrder(Order order);

    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

    Integer createOrderDiscount(OrderDiscount orderDiscount);

    void createOrderDiscountRoles(Integer discountId, List<Integer> discountIdList);

    void createOrderDiscountProducts(Integer discountId, List<Integer> productIdList);

    void createOrderDiscountProductCategories(Integer discountId, List<ProductCategory> ProductCategoryList);

    void addOrderDiscountForMemberId(Integer discountId, Integer memberId);

    void removeOrderDiscountForMemberId(Integer discountId, Integer memberId);

    Integer createOrderDiscountUsage(OrderDiscountUsage orderDiscountUsage);

    List<Integer> createOrderDiscountUsages(List<OrderDiscountUsage> orderDiscountUsages);

    Integer createPaymentMethod(PaymentMethod paymentMethod);

    Integer createShippingMethod(ShippingMethod shippingMethod);

    void createInvoice(Invoice invoice);

    void updateOrder(Order order);

    Order updateOrderStatus(Order order);

    void updateInvoice(Invoice invoice);

    ShippingMethod updateShippingMethod(ShippingMethod shippingMethod);

    PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod);

    OrderDiscount updateOrderDiscount(OrderDiscount orderDiscount);

}
