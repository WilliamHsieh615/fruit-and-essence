package com.williamhsieh.fruitandessence.controller;

import com.williamhsieh.fruitandessence.constant.OrderStatus;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Order;
import com.williamhsieh.fruitandessence.model.OrderDiscount;
import com.williamhsieh.fruitandessence.model.PaymentMethod;
import com.williamhsieh.fruitandessence.model.ShippingMethod;
import com.williamhsieh.fruitandessence.service.OrderService;
import com.williamhsieh.fruitandessence.util.PageUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/members/{memberId}/orders")
    public ResponseEntity<PageUtil<OrderResponse>> getOrders(
            @PathVariable Integer memberId,
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset

    ){

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setMemberId(memberId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        // 取得 order list
        List<OrderResponse> orderResponseList = orderService.getOrders(orderQueryParams);

        // 取得 order 總數
        Integer count = orderService.countOrders(orderQueryParams);

        // 分頁
        PageUtil<OrderResponse> pageUtil = new PageUtil<>();
        pageUtil.setLimit(limit);
        pageUtil.setOffset(offset);
        pageUtil.setTotal(count);
        pageUtil.setResults(orderResponseList);

        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<PageUtil<Order>> getAllOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "50") @Max(1000) @Min(1) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {

        AdminOrderQueryParams adminOrderQueryParams = new AdminOrderQueryParams();

        if (orderStatus != null) {
            adminOrderQueryParams.setOrderStatus(orderStatus);
        }
        if (startDate != null) {
            adminOrderQueryParams.setStartDate(LocalDateTime.parse(startDate));
        }
        if (endDate != null) {
            adminOrderQueryParams.setEndDate(LocalDateTime.parse(endDate));
        }
        adminOrderQueryParams.setLimit(limit);
        adminOrderQueryParams.setOffset(offset);

        // 從 Service 取得訂單列表
        List<Order> orderList = orderService.getAllOrders(adminOrderQueryParams);

        // 從 Service 取得總數
        Integer count = orderService.countAllOrders(adminOrderQueryParams);

        // 分頁封裝
        PageUtil<Order> pageUtil = new PageUtil<>();
        pageUtil.setLimit(limit);
        pageUtil.setOffset(offset);
        pageUtil.setTotal(count);
        pageUtil.setResults(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
    }

    @GetMapping("/members/{memberId}/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer memberId,
                                              @PathVariable Integer orderId) {

        OrderResponse orderResponse = orderService.getOrderById(memberId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    @PostMapping("/members/{memberId}/prepare")
    public ResponseEntity<OrderSummaryResponse> prepareOrder(
            @PathVariable Integer memberId,
            @RequestBody @Valid PrepareOrderRequest prepareOrderRequest) {

        OrderSummaryResponse orderSummaryResponse = orderService.prepareOrder(memberId, prepareOrderRequest);

        return ResponseEntity.status(HttpStatus.OK).body(orderSummaryResponse);
    }

    @PostMapping("/members/{memberId}/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable Integer memberId,
            @RequestBody @Valid CreatedOrderRequest createdOrderRequest) {
        Integer orderId = orderService.createOrder(memberId, createdOrderRequest);

        OrderResponse orderResponse = orderService.getOrderById(memberId, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @PutMapping("/members/{memberId}/orders/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer memberId,
                                             @PathVariable Integer orderId,
                                             @RequestBody @Valid UpdateOrderRequest updateOrderRequest) {

        Order order = orderService.updateOrder(memberId, orderId, updateOrderRequest);

        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("/members/{memberId}/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer memberId,
                                             @PathVariable Integer orderId,
                                             @RequestBody @Valid UpdateOrderStatusRequest updateOrderStatusRequest) {

        Order order = orderService.updateOrderStatus(memberId, orderId, updateOrderStatusRequest);

        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("/members/{memberId}/orders/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Integer memberId,
                                             @PathVariable Integer orderId) {

        Order order = orderService.cancelOrder(memberId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @GetMapping("/admin/shipping-method")
    public ResponseEntity<List<ShippingMethod>> getShippingMethods() {

        List<ShippingMethod> shippingMethodList = orderService.getShippingMethods();

        return ResponseEntity.status(HttpStatus.OK).body(shippingMethodList);
    }

    @GetMapping("/admin/payment-method")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethods() {

        List<PaymentMethod> paymentMethodList = orderService.getPaymentMethods();

        return ResponseEntity.status(HttpStatus.OK).body(paymentMethodList);
    }

    @GetMapping("/admin/order-discount")
    public ResponseEntity<List<OrderDiscount>> getOrderDiscounts() {

        List<OrderDiscount> orderDiscountList = orderService.getOrderDiscounts();

        return ResponseEntity.status(HttpStatus.OK).body(orderDiscountList);
    }

    @PostMapping("/admin/shipping-method")
    public ResponseEntity<String> createShippingMethod(@RequestBody ShippingMethod shippingMethod) {

        String result = orderService.createShippingMethod(shippingMethod);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/admin/payment-method")
    public ResponseEntity<String> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {

        String result = orderService.createPaymentMethod(paymentMethod);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/admin/order-discount")
    public ResponseEntity<String> createOrderDiscount(@RequestBody OrderDiscount orderDiscount) {

        String result = orderService.createOrderDiscount(orderDiscount);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/admin/shipping-method/{methodId}")
    public ResponseEntity<String> updateShippingMethod(@PathVariable Integer methodId,
                                     @RequestBody ShippingMethod shippingMethod) {

        String result = orderService.updateShippingMethod(methodId, shippingMethod);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/admin/payment-method/{methodId}")
    public ResponseEntity<String> updatePaymentMethod(@PathVariable Integer methodId,
                                    @RequestBody PaymentMethod paymentMethod) {

        String result = orderService.updatePaymentMethod(methodId, paymentMethod);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/admin/order-discount/{discountId}")
    public ResponseEntity<String> updateOrderDiscount(@PathVariable Integer discountId,
                                    @RequestBody OrderDiscount orderDiscount) {

        String result = orderService.updateOrderDiscount(discountId, orderDiscount);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
