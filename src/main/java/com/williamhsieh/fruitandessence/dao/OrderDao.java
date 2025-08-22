package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.model.OrderItem;

import java.util.List;

public interface OrderDao {

    Integer createOrder(Integer memberId, Integer totalAmount, CreatedOrderRequest createdOrderRequest);
    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

}
