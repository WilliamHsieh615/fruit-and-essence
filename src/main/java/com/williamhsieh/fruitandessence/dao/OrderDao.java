package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderItem;

import java.util.List;

public interface OrderDao {

    Order getOrderById(Integer orderId);

    List<OrderItemResponse> getOrderItemsById(Integer orderId);

    Integer createOrder(Integer memberId, Integer totalAmount, CreatedOrderRequest createdOrderRequest);

    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

}
