package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.model.Order;

public interface OrderService {

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest);

}
