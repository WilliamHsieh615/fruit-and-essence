package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dao.OrderDao;
import com.williamhsieh.fruitandessence.dao.ProductDao;
import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderItemResponse;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.dto.PurchaseItem;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderItem;
import com.williamhsieh.fruitandessence.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private MemberDao memberDao;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        return orderDao.countOrder(orderQueryParams);
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        for (Order order : orderList) {
            List<OrderItemResponse> orderItemListResponse = orderDao.getOrderItemsByOrderId(order.getOrderId());

            order.setOrderItemListResponse(orderItemListResponse);
        }

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {

        Order order = orderDao.getOrderById(orderId);

        List<OrderItemResponse> orderItemListResponse = orderDao.getOrderItemsByOrderId(orderId);

        order.setOrderItemListResponse(orderItemListResponse);

        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest) {

        // 檢查 member 是否存在
        Member member = memberDao.getMemberById(memberId);

        if(member == null){
            log.warn("{} 非使用者", memberId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        int totalAmount = 0;

        List<OrderItem> orderItemList = new ArrayList<>();

        for (PurchaseItem purchaseItem : createdOrderRequest.getPurchaseItemList()){

            Product product = productDao.getProductById(purchaseItem.getProductId());

            // 檢查 product 是否存在、庫存是否足夠
            if(product == null){
                log.warn("商品 {} 不存在", purchaseItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            Double newStock = null;

            if (purchaseItem.getPurchasedCount() != null) {
                // 數量型商品
                if (product.getStock() < purchaseItem.getPurchasedCount()) {
                    log.warn("商品 {} 庫存不足，欲購買數量 {}，剩餘庫存 {}",
                            product.getProductName(), purchaseItem.getPurchasedCount(), product.getStock());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "庫存不足");
                }
                newStock = product.getStock() - purchaseItem.getPurchasedCount();

            } else if (purchaseItem.getPurchasedWeight() != null) {
                // 重量型商品
                if (product.getStock() < purchaseItem.getPurchasedWeight()) {
                    log.warn("商品 {} 庫存不足，欲購買重量 {}，剩餘庫存 {}",
                            product.getProductName(), purchaseItem.getPurchasedWeight(), product.getStock());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "庫存不足");
                }
                newStock = product.getStock() - purchaseItem.getPurchasedWeight();

            }

            // 扣除商品庫存
            if (newStock != null) {
                productDao.updateStock(product.getProductId(), newStock);
            }

            // 計算總價錢
            double itemAmount = 0;

            if (purchaseItem.getPurchasedCount() != null) {
                // COUNT 型商品，乘以每顆價格
                itemAmount = purchaseItem.getPurchasedCount() * product.getPricePerUnit();
            } else if (purchaseItem.getPurchasedWeight() != null) {
                // WEIGHT 型商品，乘以每公斤價格
                itemAmount = purchaseItem.getPurchasedWeight() * product.getPricePerUnit();
            }

            totalAmount += ((int)Math.round(itemAmount));

            // 把 PurchaseItem 轉換成 OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(purchaseItem.getProductId());
            orderItem.setPurchasedCount(purchaseItem.getPurchasedCount());
            orderItem.setPurchasedWeight(purchaseItem.getPurchasedWeight());
            orderItem.setAmount((int)Math.round(itemAmount));

            orderItemList.add(orderItem);

        }

        // 創建訂單
        Integer orderId = orderDao.createOrder(memberId, totalAmount, createdOrderRequest);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
