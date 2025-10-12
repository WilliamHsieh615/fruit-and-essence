package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dao.OrderDao;
import com.williamhsieh.fruitandessence.dao.ProductDao;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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
    public Integer countOrders(OrderQueryParams orderQueryParams) {

        return orderDao.countOrders(orderQueryParams);
    }

    @Override
    public List<OrderResponse> getOrders(OrderQueryParams orderQueryParams) {

        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        List<OrderResponse> orderResponseList = new ArrayList<>();

        for (Order order : orderList) {
            OrderResponse orderResponse = buildOrderResponse(order);
            orderResponseList.add(orderResponse);
        }

        return orderResponseList;
    }

    @Override
    public Integer countAllOrders(AdminOrderQueryParams adminOrderQueryParams) {

        return orderDao.countAllOrders(adminOrderQueryParams);
    }

    @Override
    public List<Order> getAllOrders(AdminOrderQueryParams adminOrderQueryParams) {

        // 取得所有 Order
        List<Order> orderList = orderDao.getAllOrders(adminOrderQueryParams);

        // 取得所有 orderId
        List<Integer> orderIds = orderList.stream()
                .map(Order::getOrderId)
                .toList();

        // 取得所有 OrderItem
        Map<Integer, List<OrderItem>> orderItemsMap = orderDao.getOrderItemsByOrderIds(orderIds);

        // 取得所有 productId
        List<Integer> productIds = orderItemsMap.values().stream()
                .flatMap(List::stream)
                .map(OrderItem::getProductId)
                .distinct()
                .toList();

        // 取得所有 productVariantId
        List<Integer> variantIds = orderItemsMap.values().stream()
                .flatMap(List::stream)
                .map(OrderItem::getProductVariantId)
                .distinct()
                .toList();

        // 取得所有 Product
        Map<Integer, Product> productMap = productDao.getProductsByIds(productIds);

        // 取得所有 ProductVariant
        Map<Integer, ProductVariant> variantMap = productDao.getVariantsByIds(variantIds);

        // 取得所有 Invoice
        Map<Integer, Invoice> invoiceMap = orderDao.getInvoicesByOrderIds(orderIds);

        // 取得所有 OrderDiscount
        Map<Integer, OrderDiscount> discountMap = orderDao.getDiscountsByOrderIds(orderIds);

        // 取得所有 PaymentMethod
        Map<Integer, PaymentMethod> paymentMap = orderDao.getPaymentMethodsByOrderIds(orderIds);

        // 取得所有 ShippingMethod
        Map<Integer, ShippingMethod> shippingMap = orderDao.getShippingMethodsByOrderIds(orderIds);

        for (Order order : orderList) {

            List<OrderItem> orderItems = orderItemsMap.getOrDefault(order.getOrderId(), List.of());

            for (OrderItem orderItem : orderItems) {
                Product product = productMap.get(orderItem.getProductId());
                ProductVariant variant = variantMap.get(orderItem.getProductVariantId());
                orderItem.setProduct(product);
                orderItem.setProductVariant(variant);
            }

            // 明細
            order.setOrderItems(orderItemsMap.getOrDefault(order.getOrderId(), List.of()));

            // 發票
            order.setInvoice(invoiceMap.get(order.getOrderId()));

            // 折扣
            order.setOrderDiscount(discountMap.get(order.getOrderId()));

            // 付款方式
            order.setPaymentMethod(paymentMap.get(order.getOrderId()));

            // 出貨方式
            order.setShippingMethod(shippingMap.get(order.getOrderId()));
        }

        return orderList;
    }

    @Override
    public OrderResponse getOrderById(Integer memberId, Integer orderId) {

        Order order = orderDao.getOrderById(orderId);

        OrderResponse orderResponse = buildOrderResponse(order);

        return orderResponse;
    }

    @Override
    public OrderSummaryResponse prepareOrder(Integer memberId, PrepareOrderRequest prepareOrderRequest) {

        // 檢查 member 是否存在
        Member member = memberDao.getMemberById(memberId);

        if(member == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        OrderSummaryResponse orderSummaryResponse = new OrderSummaryResponse();
        orderSummaryResponse.setMemberId(memberId);
        orderSummaryResponse.setSubtotal();
        orderSummaryResponse.setTaxAmount();
        orderSummaryResponse.setDiscountAmount();


        return null;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest) {

        // 檢查 member 是否存在
        Member member = memberDao.getMemberById(memberId);

        if(member == null){
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

    @Override
    public Order updateOrder(Integer memberId, Integer orderId, UpdateOrderRequest updateOrderRequest) {
        return null;
    }

    @Override
    public Order updateOrderStatus(Integer memberId, Integer orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        return null;
    }

    @Override
    public Order cancelOrder(Integer memberId, Integer orderId) {
        return null;
    }

    @Override
    public List<ShippingMethod> getShippingMethods() {
        return List.of();
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return List.of();
    }

    @Override
    public List<OrderDiscount> getOrderDiscounts() {
        return List.of();
    }

    @Override
    public String createShippingMethod(ShippingMethod shippingMethod) {
        return "";
    }

    @Override
    public String createPaymentMethod(PaymentMethod paymentMethod) {
        return "";
    }

    @Override
    public String createOrderDiscount(OrderDiscount orderDiscount) {
        return "";
    }

    @Override
    public String updateShippingMethod(Integer methodId, ShippingMethod shippingMethod) {
        return "";
    }

    @Override
    public String updatePaymentMethod(Integer methodId, PaymentMethod paymentMethod) {
        return "";
    }

    @Override
    public String updateOrderDiscount(Integer discountId, OrderDiscount orderDiscount) {
        return "";
    }

    private OrderResponse buildOrderResponse(Order order) {

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setMemberId(order.getMemberId());

        orderResponse.setSubtotal(order.getSubtotal());
        orderResponse.setTaxAmount(order.getTaxAmount());
        orderResponse.setDiscountAmount(order.getDiscountAmount());
        orderResponse.setShippingFee(order.getShippingFee());
        orderResponse.setTotalAmount(order.getTotalAmount());

        orderResponse.setShippingPhone(order.getShippingPhone());
        orderResponse.setShippingAddress(order.getShippingAddress());
        orderResponse.setShippingNote(order.getShippingNote());

        PaymentMethod paymentMethod = orderDao.getPaymentMethodById(order.getPaymentMethodId());
        orderResponse.setPaymentMethod(paymentMethod.getMethodName());

        ShippingMethod shippingMethod = orderDao.getShippingMethodById(order.getShippingMethodId());
        orderResponse.setShippingMethod(shippingMethod.getMethodName());
        orderResponse.setShippingProviderCode(shippingMethod.getProviderCode());

        orderResponse.setOrderStatus(order.getOrderStatus());
        orderResponse.setShippingDate(order.getShippingDate());
        orderResponse.setTrackingNumber(order.getTrackingNumber());
        orderResponse.setCancelReason(order.getCancelReason());

        orderResponse.setCreatedDate(order.getCreatedDate());
        orderResponse.setLastModifiedDate(order.getLastModifiedDate());

        Invoice invoice = orderDao.getInvoiceByOrderId(order.getOrderId());
        InvoiceResponse invoiceResponse = new InvoiceResponse();

        invoiceResponse.setInvoiceId(invoice.getInvoiceId());
        invoiceResponse.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceResponse.setInvoiceCarrier(invoice.getInvoiceCarrier());
        invoiceResponse.setInvoiceDonationCode(invoice.getInvoiceDonationCode());
        invoiceResponse.setCompanyTaxId(invoice.getCompanyTaxId());
        invoiceResponse.setIssued(invoice.getIssued());
        invoiceResponse.setIssuedDate(invoice.getIssuedDate());
        invoiceResponse.setCreatedDate(invoice.getCreatedDate());
        invoiceResponse.setLastModifiedDate(invoice.getLastModifiedDate());

        orderResponse.setInvoice(invoiceResponse);

        List<OrderDiscount> orderDiscountList = orderDao.getOrderDiscountsByOrderId(order.getOrderId());
        List<OrderDiscountResponse> orderDiscountResponseList = new ArrayList<>();

        for (OrderDiscount orderDiscount : orderDiscountList) {
            OrderDiscountResponse orderDiscountResponse = new OrderDiscountResponse();

            orderDiscountResponse.setDiscountName(orderDiscount.getDiscountName());
            orderDiscountResponse.setDiscountCode(orderDiscount.getDiscountCode());
            orderDiscountResponse.setDiscountType(orderDiscount.getDiscountType());
            orderDiscountResponse.setDiscountValue(orderDiscount.getDiscountValue());
            orderDiscountResponse.setDiscountPercentage(orderDiscount.getDiscountPercentage());
            orderDiscountResponse.setMinOrderAmount(orderDiscount.getMinOrderAmount());
            orderDiscountResponse.setStartDate(orderDiscount.getStartDate());
            orderDiscountResponse.setEndDate(orderDiscount.getEndDate());

            orderDiscountResponseList.add(orderDiscountResponse);
        }

        orderResponse.setOrderDiscounts(orderDiscountResponseList);

        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(order.getOrderId());
        List<OrderItemResponse> orderItemResponseList = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {
            OrderItemResponse orderItemResponse = new OrderItemResponse();

            orderItemResponse.setOrderItemId(orderItem.getOrderItemId());
            orderItemResponse.setProductId(orderItem.getProductId());
            orderItemResponse.setProductVariantId(orderItem.getProductVariantId());

            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponse.setPrice(orderItem.getPrice());
            orderItemResponse.setItemTotal(orderItem.getItemTotal());
            orderItemResponse.setNotes(orderItem.getNotes());

            Product product = productDao.getProductById(orderItem.getProductId());
            orderItemResponse.setProductName(product.getProductName());
            orderItemResponse.setProductCategory(product.getProductCategory());

            ProductVariant productVariant = productDao.getVariantById(orderItem.getProductVariantId());
            orderItemResponse.setProductSize(productVariant.getProductSize().toString());
            orderItemResponse.setUnit(productVariant.getUnit());

            List<String> productImageList = productDao.getImagesByProductId(orderItem.getProductId());
            orderItemResponse.setProductImage(productImageList.get(0));

            orderItemResponseList.add(orderItemResponse);
        }

        orderResponse.setOrderItems(orderItemResponseList);

        return orderResponse;
    }

}
