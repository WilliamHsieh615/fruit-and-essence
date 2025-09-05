package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.Order;

import java.util.List;

public interface OrderService {

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest);

}
