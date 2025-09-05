package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderItem;

import java.util.List;

public interface OrderDao {

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    List<OrderItemResponse> getOrderItemsByOrderId(Integer orderId);

    Integer createOrder(Integer memberId, Integer totalAmount, CreatedOrderRequest createdOrderRequest);

    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

}
