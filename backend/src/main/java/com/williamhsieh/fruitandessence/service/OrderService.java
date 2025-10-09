package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderDiscount;
import com.williamhsieh.fruitandessence.model.PaymentMethod;
import com.williamhsieh.fruitandessence.model.ShippingMethod;

import java.util.List;

public interface OrderService {

    Integer countOrders(OrderQueryParams orderQueryParams);

    List<OrderResponse> getOrders(OrderQueryParams orderQueryParams);

    Integer countAllOrders(AdminOrderQueryParams adminOrderQueryParams);

    List<Order> getAllOrders(AdminOrderQueryParams adminOrderQueryParams);

    OrderResponse getOrderById(Integer memberId, Integer orderId);

    OrderSummaryResponse prepareOrder(Integer memberId, PrepareOrderRequest prepareOrderRequest);

    Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest);

    Order updateOrder(Integer memberId, Integer orderId, UpdateOrderRequest updateOrderRequest);

    Order updateOrderStatus(Integer memberId, Integer orderId, UpdateOrderStatusRequest updateOrderStatusRequest);

    Order cancelOrder(Integer memberId, Integer orderId);

    List<ShippingMethod> getShippingMethods();

    List<PaymentMethod> getPaymentMethods();

    List<OrderDiscount> getOrderDiscounts();

    String createShippingMethod(ShippingMethod shippingMethod);

    String createPaymentMethod(PaymentMethod paymentMethod);

    String createOrderDiscount(OrderDiscount orderDiscount);

    String updateShippingMethod(Integer methodId, ShippingMethod shippingMethod);

    String updatePaymentMethod(Integer methodId, PaymentMethod paymentMethod);

    String updateOrderDiscount(Integer discountId, OrderDiscount orderDiscount);

}
