package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.OrderDao;
import com.williamhsieh.fruitandessence.dao.ProductDao;
import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.dto.PurchaseItem;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderItem;
import com.williamhsieh.fruitandessence.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Override
    public Order getOrderById(Integer orderId) {

        Order order = orderDao.getOrderById(orderId);

        List<OrderItemResponse> orderItemListResponse = orderDao.getOrderItemsById(orderId);

        order.setOrderItemListResponse(orderItemListResponse);

        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest) {

        int totalAmount = 0;

        List<OrderItem> orderItemList = new ArrayList<>();

        for (PurchaseItem purchaseItem : createdOrderRequest.getPurchaseItemList()){

            Product product = productDao.getProductById(purchaseItem.getProductId());

            double itemAmount = 0;

            if (purchaseItem.getPurchasedCount() != null) {
                // COUNT 型商品，乘以每顆價格
                itemAmount = purchaseItem.getPurchasedCount() * product.getPricePerUnit();
            } else if (purchaseItem.getPurchasedWeight() != null) {
                // WEIGHT 型商品，乘以每公斤價格
                itemAmount = purchaseItem.getPurchasedWeight() * product.getPricePerUnit();
            }

            totalAmount += ((int)Math.round(itemAmount));

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(purchaseItem.getProductId());
            orderItem.setPurchasedCount(purchaseItem.getPurchasedCount());
            orderItem.setPurchasedWeight(purchaseItem.getPurchasedWeight());
            orderItem.setAmount((int)Math.round(itemAmount));

            orderItemList.add(orderItem);

        }

        Integer orderId = orderDao.createOrder(memberId, totalAmount, createdOrderRequest);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
