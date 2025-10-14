package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.constant.StockChangeReason;
import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dao.OrderDao;
import com.williamhsieh.fruitandessence.dao.ProductDao;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public Integer countAllOrders(AdminOrderQueryParams adminOrderQueryParams) {

        return orderDao.countAllOrders(adminOrderQueryParams);
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
        Map<Integer, ProductVariant> productVariantMap = productDao.getVariantsByIds(variantIds);

        // 取得所有 Invoice
        Map<Integer, Invoice> invoiceMap = orderDao.getInvoicesByOrderIds(orderIds);

        // 取得所有 OrderDiscount
        Map<Integer, List<OrderDiscount>> discountMap = orderDao.getOrderDiscountsByOrderIds(orderIds);

        for (Order order : orderList) {

            List<OrderItem> orderItems = orderItemsMap.getOrDefault(order.getOrderId(), List.of());

            for (OrderItem orderItem : orderItems) {
                Product product = productMap.get(orderItem.getProductId());
                ProductVariant productVariant = productVariantMap.get(orderItem.getProductVariantId());
                orderItem.setProduct(product);
                orderItem.setProductVariant(productVariant);
            }

            // 明細
            order.setOrderItems(orderItemsMap.getOrDefault(order.getOrderId(), List.of()));

            // 發票
            order.setInvoice(invoiceMap.get(order.getOrderId()));

            // 折扣
            order.setOrderDiscounts(discountMap.get(order.getOrderId()));

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

        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        OrderSummaryResponse orderSummaryResponse = new OrderSummaryResponse();
        orderSummaryResponse.setMemberId(memberId);

        // OrderDiscountResponseList
        List<OrderDiscount> orderDiscountList = orderDao.getOrderDiscountsByMemberId(memberId);
        List<OrderDiscountResponse> orderDiscountResponseList = new ArrayList<>();
        for (OrderDiscount orderDiscount : orderDiscountList) {
            OrderDiscountResponse orderDiscountResponse = new OrderDiscountResponse();
            orderDiscountResponse.setDiscountName(orderDiscount.getDiscountName());
            orderDiscountResponse.setDiscountCode(orderDiscount.getDiscountCode());
            orderDiscountResponse.setDiscountType(orderDiscount.getDiscountType());
            orderDiscountResponse.setDiscountPercentage(orderDiscount.getDiscountPercentage());
            orderDiscountResponse.setMinOrderAmount(orderDiscount.getMinOrderAmount());
            orderDiscountResponse.setStartDate(orderDiscount.getStartDate());
            orderDiscountResponse.setEndDate(orderDiscount.getEndDate());

            orderDiscountResponseList.add(orderDiscountResponse);
        }

        orderSummaryResponse.setOrderDiscountResponseList(orderDiscountResponseList);

        // Subtotal
        BigDecimal fontSubtotal = prepareOrderRequest.getSubtotal();
        BigDecimal backSubtotal = BigDecimal.ZERO;

        // ShoppingListResponse
        List<CartItemRequest> cartItemRequestList = prepareOrderRequest.getCartItems();
        List<ShoppingListResponse> shoppingListResponseList = new ArrayList<>();
        for (CartItemRequest cartItemRequest : cartItemRequestList) {

            // OrderSummaryResponse 的 Subtotal
            BigDecimal realPrice = (cartItemRequest.getDiscountPrice() != null) ? cartItemRequest.getDiscountPrice() : cartItemRequest.getPrice();
            BigDecimal itemTotal = realPrice.multiply(BigDecimal.valueOf(cartItemRequest.getQuantity()));
            backSubtotal = backSubtotal.add(itemTotal);

            ShoppingListResponse shoppingListResponse = new ShoppingListResponse();
            shoppingListResponse.setProductId(cartItemRequest.getProductId());
            shoppingListResponse.setProductVariantId(cartItemRequest.getProductVariantId());

            shoppingListResponse.setProductImage(cartItemRequest.getProductImage());
            shoppingListResponse.setProductCategory(cartItemRequest.getProductCategory());
            shoppingListResponse.setProductName(cartItemRequest.getProductName());
            shoppingListResponse.setProductSize(cartItemRequest.getProductSize());
            shoppingListResponse.setQuantity(cartItemRequest.getQuantity());
            shoppingListResponse.setUnit(cartItemRequest.getUnit());
            shoppingListResponse.setPrice(cartItemRequest.getPrice());
            shoppingListResponse.setDiscountPrice(cartItemRequest.getDiscountPrice());
            shoppingListResponse.setRealPrice(realPrice);
            shoppingListResponse.setItemTotal(itemTotal);

            shoppingListResponseList.add(shoppingListResponse);
        }

        if (fontSubtotal.equals(backSubtotal)) {
            orderSummaryResponse.setSubtotal(fontSubtotal);
        } else {
            log.warn("Provided subtotal {} does not match calculated subtotal {}", fontSubtotal, backSubtotal);
            orderSummaryResponse.setSubtotal(backSubtotal);
        }

        // TaxAmount
        BigDecimal taxRate = new BigDecimal("0.05"); // 消費税 5%
        BigDecimal subtotal = orderSummaryResponse.getSubtotal();
        BigDecimal taxAmount = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        orderSummaryResponse.setTaxAmount(taxAmount);

        orderSummaryResponse.setShoppingListResponse(shoppingListResponseList);

        return orderSummaryResponse;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer memberId, CreatedOrderRequest createdOrderRequest) {

        // 檢查 member 是否存在
        Member member = memberDao.getMemberById(memberId);

        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setMemberId(memberId);
        order.setSubtotal(createdOrderRequest.getSubtotal());
        order.setTaxAmount(createdOrderRequest.getTaxAmount());
        order.setDiscountAmount(createdOrderRequest.getDiscountAmount());
        order.setShippingFee(createdOrderRequest.getShippingFee());

        BigDecimal fontTotalAmount = createdOrderRequest.getTotalAmount();
        BigDecimal backTotalAmount = createdOrderRequest.getSubtotal()
                .add(createdOrderRequest.getTaxAmount())
                .add(createdOrderRequest.getShippingFee())
                .subtract(createdOrderRequest.getDiscountAmount() == null ? BigDecimal.ZERO : createdOrderRequest.getDiscountAmount());


        if (fontTotalAmount.equals(backTotalAmount)) {
            order.setTotalAmount(fontTotalAmount);
        } else {
            log.warn("Provided total {} does not match calculated total {}", fontTotalAmount, backTotalAmount);
            order.setTotalAmount(backTotalAmount);
        }

        order.setShippingPhone(createdOrderRequest.getShippingPhone());
        order.setShippingAddress(createdOrderRequest.getShippingAddress());
        order.setShippingNote(createdOrderRequest.getShippingNote());

        order.setPaymentMethodId(createdOrderRequest.getPaymentMethodId());
        order.setShippingMethodId(createdOrderRequest.getShippingMethodId());

        order.setOrderStatus(OrderStatus.PENDING); // 由後台變更
        LocalDateTime shippingDateTime = LocalDateTime.of(LocalDateTime.now().plusDays(2).toLocalDate(), LocalTime.of(10, 0));
        order.setShippingDate(shippingDateTime);
        order.setTrackingNumber(""); // 由後台變更
        order.setCancelReason(""); // 由後台變更

        LocalDateTime now = LocalDateTime.now();
        order.setCreatedDate(now);
        order.setLastModifiedDate(now);

        Integer orderId = orderDao.createOrder(order);

        List<String> discountCodeList = createdOrderRequest.getDiscountCodes();
        List<OrderDiscountUsage> orderDiscountUsageList = new ArrayList<>();
        for (String discountCode : discountCodeList) {
            OrderDiscount orderDiscount = orderDao.getOrderDiscountByDiscountCode(discountCode);
            OrderDiscountUsage  orderDiscountUsage = new OrderDiscountUsage();
            orderDiscountUsage.setDiscountId(orderDiscount.getDiscountId());
            orderDiscountUsage.setMemberId(memberId);
            orderDiscountUsage.setUsedAt(now);
            orderDiscountUsage.setOrderId(orderId);

            orderDiscountUsageList.add(orderDiscountUsage);
        }

        List<Integer> orderDiscountUsageIdList = orderDao.createOrderDiscountUsages(orderDiscountUsageList);

        InvoiceRequest invoiceRequest = createdOrderRequest.getInvoiceRequest();
        Invoice invoice = new Invoice();
        invoice.setOrderId(orderId);
        invoice.setInvoiceNumber("要呼叫財政部電子發票 API 取得 InvoiceNumber");
        invoice.setInvoiceCarrier(invoiceRequest.getInvoiceCarrier());
        invoice.setInvoiceDonationCode(invoiceRequest.getInvoiceDonationCode());
        invoice.setCompanyTaxId(invoiceRequest.getCompanyTaxId());
        invoice.setIssued(false); // 由後台變更
        invoice.setIssuedDate(null); // 由後台變更
        invoice.setCreatedDate(now);
        invoice.setLastModifiedDate(now);

        orderDao.createInvoice(invoice);

        List<CreatedOrderItemRequest> createdOrderItemRequestList = createdOrderRequest.getCreatedOrderItems();
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CreatedOrderItemRequest createdOrderItemRequest : createdOrderItemRequestList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(createdOrderItemRequest.getProductId());
            orderItem.setProductVariantId(createdOrderItemRequest.getProductVariantId());
            orderItem.setQuantity(createdOrderItemRequest.getQuantity());
            orderItem.setPrice(createdOrderItemRequest.getRealPrice());
            orderItem.setItemTotal(createdOrderItemRequest.getItemTotal());
            orderItem.setNotes(""); // 由後台變更

            orderItemList.add(orderItem);

            List<StockHistory> stockHistoryList = productDao.getStockHistoryByProductVariantId(createdOrderItemRequest.getProductVariantId());
            StockHistory lastStockHistory = stockHistoryList.get(stockHistoryList.size() - 1);
            Integer newStock = lastStockHistory.getStockAfter()-createdOrderItemRequest.getQuantity();

            StockHistory stockHistory = new StockHistory();
            stockHistory.setProductVariantId(createdOrderItemRequest.getProductVariantId());
            stockHistory.setChangeAmount(-(createdOrderItemRequest.getQuantity()));
            stockHistory.setStockAfter(newStock);
            stockHistory.setStockChangeReason(StockChangeReason.ORDER);
            stockHistory.setCreatedDate(now);

            productDao.insertStockHistory(stockHistory);

            productDao.updateProductVariantStock(createdOrderItemRequest.getProductVariantId(), newStock);
        }

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }

    @Override
    public Order updateOrderStatus(Integer memberId, Integer orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {

        Member member = memberDao.getMemberById(memberId);

        if(member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Order order = orderDao.getOrderById(orderId);

        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        if (!order.getMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to this member");
        }

        order.setOrderStatus(updateOrderStatusRequest.getOrderStatus());
        order.setLastModifiedDate(LocalDateTime.now());

        orderDao.updateOrderStatus(order);

        return orderDao.getOrderById(orderId);
    }

    @Transactional
    @Override
    public Order cancelOrder(Integer memberId, Integer orderId, String cancelReason) {

        Member member = memberDao.getMemberById(memberId);
        if(member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member not found");
        }
        Order order = orderDao.getOrderById(orderId);
        if(order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelReason);
        order.setShippingDate(null);
        order.setLastModifiedDate(LocalDateTime.now());

        orderDao.updateOrder(order);

        Invoice invoice = orderDao.getInvoiceByOrderId(orderId);
        invoice.setInvoiceNumber(null);
        invoice.setIssued(false);
        invoice.setIssuedDate(null);
        invoice.setLastModifiedDate(LocalDateTime.now());

        orderDao.updateInvoice(invoice);

        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);
        for(OrderItem orderItem : orderItemList) {
            StockHistory stockHistory = new StockHistory();
            stockHistory.setProductVariantId(orderItem.getProductVariantId());
            stockHistory.setChangeAmount(orderItem.getQuantity());

            List<StockHistory> stockHistoryList = productDao.getStockHistoryByProductVariantId(orderItem.getProductVariantId());
            StockHistory lastStockHistory = stockHistoryList.get(stockHistoryList.size() - 1);
            Integer newStock = lastStockHistory.getStockAfter() + orderItem.getQuantity();

            stockHistory.setStockAfter(newStock);
            stockHistory.setStockChangeReason(StockChangeReason.RETURN);
            stockHistory.setCreatedDate(LocalDateTime.now());

            productDao.insertStockHistory(stockHistory);

            productDao.updateProductVariantStock(orderItem.getProductVariantId(), newStock);
        }

        return orderDao.getOrderById(orderId);
    }

    @Override
    public List<ShippingMethod> getShippingMethods() {

        return orderDao.getShippingMethods();
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {

        return orderDao.getPaymentMethods();
    }

    @Override
    public List<OrderDiscount> getOrderDiscounts() {
        return orderDao.getOrderDiscounts();
    }

    @Override
    public Integer createShippingMethod(ShippingMethod shippingMethod) {

        return orderDao.createShippingMethod(shippingMethod);
    }

    @Override
    public Integer createPaymentMethod(PaymentMethod paymentMethod) {

        return orderDao.createPaymentMethod(paymentMethod);
    }

    @Override
    public Integer createOrderDiscount(OrderDiscount orderDiscount) {

        return orderDao.createOrderDiscount(orderDiscount);
    }

    @Override
    public ShippingMethod updateShippingMethod(Integer shippingMethodId, ShippingMethod shippingMethod) {

        ShippingMethod oldShippingMethod = orderDao.getShippingMethodById(shippingMethodId);

        if (oldShippingMethod == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ShippingMethod not found");
        }

        oldShippingMethod.setShippingMethodName(shippingMethod.getShippingMethodName());
        oldShippingMethod.setProviderCode(shippingMethod.getProviderCode());
        oldShippingMethod.setDescription(shippingMethod.getDescription());

        ShippingMethod newShippingMethod = orderDao.updateShippingMethod(oldShippingMethod);

        return newShippingMethod;
    }

    @Override
    public PaymentMethod updatePaymentMethod(Integer paymentMethodId, PaymentMethod paymentMethod) {

        PaymentMethod oldPaymentMethod = orderDao.getPaymentMethodById(paymentMethodId);

        if (oldPaymentMethod == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentMethod not found");
        }

        oldPaymentMethod.setPaymentMethodName(paymentMethod.getPaymentMethodName());
        oldPaymentMethod.setDescription(paymentMethod.getDescription());

        PaymentMethod newPaymentMethod = orderDao.updatePaymentMethod(oldPaymentMethod);

        return newPaymentMethod;
    }

    @Override
    public OrderDiscount updateOrderDiscount(Integer discountId, OrderDiscount orderDiscount) {

        OrderDiscount oldOrderDiscount = orderDao.getOrderDiscountById(discountId);

        if (oldOrderDiscount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderDiscount not found");
        }

        oldOrderDiscount.setDiscountName(orderDiscount.getDiscountName());
        oldOrderDiscount.setDiscountCode(orderDiscount.getDiscountCode());
        oldOrderDiscount.setDiscountType(orderDiscount.getDiscountType());
        oldOrderDiscount.setDiscountValue(orderDiscount.getDiscountValue());
        oldOrderDiscount.setDiscountPercentage(orderDiscount.getDiscountPercentage());
        oldOrderDiscount.setMinOrderAmount(orderDiscount.getMinOrderAmount());
        oldOrderDiscount.setTotalUsageLimit(orderDiscount.getTotalUsageLimit());
        oldOrderDiscount.setStartDate(orderDiscount.getStartDate());
        oldOrderDiscount.setEndDate(orderDiscount.getEndDate());
        oldOrderDiscount.setCreatedDate(orderDiscount.getCreatedDate());
        oldOrderDiscount.setLastModifiedDate(LocalDateTime.now());

        OrderDiscount newOrderDiscount = orderDao.updateOrderDiscount(oldOrderDiscount);

        return newOrderDiscount;
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
        orderResponse.setPaymentMethod(paymentMethod.getPaymentMethodName());

        ShippingMethod shippingMethod = orderDao.getShippingMethodById(order.getShippingMethodId());
        orderResponse.setShippingMethod(shippingMethod.getShippingMethodName());
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

    private static int sequence = 0;
    private static LocalDateTime lastSecond = LocalDateTime.now().withNano(0);
    private String generateOrderNumber() {

        LocalDateTime now = LocalDateTime.now().withNano(0);

        if (!now.equals(lastSecond)) {
            sequence = 0;
            lastSecond = now;
        }

        sequence++;

        String prefix = "FE";
        String dateTimePart = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String sequencePart = String.format("%03d", sequence);
        String randomPart = String.format("%02X", new Random().nextInt(256));

        return prefix + dateTimePart + sequencePart + randomPart;
    }

}
