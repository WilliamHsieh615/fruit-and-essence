package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;

public interface OrderService {

    Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest);

}
