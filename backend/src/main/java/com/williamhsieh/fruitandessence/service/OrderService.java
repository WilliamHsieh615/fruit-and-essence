package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderDiscount;
import com.williamhsieh.fruitandessence.model.PaymentMethod;
import com.williamhsieh.fruitandessence.model.ShippingMethod;

import java.util.List;

public interface OrderService {

    Integer countOrders(OrderQueryParams orderQueryParams);

    Integer countAllOrders(AdminOrderQueryParams adminOrderQueryParams);

    List<OrderResponse> getOrders(OrderQueryParams orderQueryParams);

    List<Order> getAllOrders(AdminOrderQueryParams adminOrderQueryParams);

    OrderResponse getOrderById(Integer memberId, Integer orderId);

    OrderSummaryResponse prepareOrder(Integer memberId, PrepareOrderRequest prepareOrderRequest);

    Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest);

    Order updateOrderStatus(Integer memberId, Integer orderId, UpdateOrderStatusRequest updateOrderStatusRequest);

    Order cancelOrder(Integer memberId, Integer orderId, String cancelReason);

    List<ShippingMethod> getShippingMethods();

    List<PaymentMethod> getPaymentMethods();

    List<OrderDiscount> getOrderDiscounts();

    Integer createShippingMethod(ShippingMethod shippingMethod);

    Integer createPaymentMethod(PaymentMethod paymentMethod);

    Integer createOrderDiscount(OrderDiscount orderDiscount);

    ShippingMethod updateShippingMethod(Integer shippingMethodId, ShippingMethod shippingMethod);

    PaymentMethod updatePaymentMethod(Integer paymentMethodId, PaymentMethod paymentMethod);

    OrderDiscount updateOrderDiscount(Integer discountId, OrderDiscount orderDiscount);

}
