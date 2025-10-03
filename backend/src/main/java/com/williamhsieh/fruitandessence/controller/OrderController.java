package com.williamhsieh.fruitandessence.controller;

import com.williamhsieh.fruitandessence.dto.CreatedOrderRequest;
import com.williamhsieh.fruitandessence.dto.OrderQueryParams;
import com.williamhsieh.fruitandessence.model.Order;
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

import java.util.List;

@Validated
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/members/{memberId}/orders")
    public ResponseEntity<PageUtil<Order>> getOrders(
            @PathVariable Integer memberId,
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset

    ){

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setMemberId(memberId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        // 取得 order list
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        // 取得 order 總數
        Integer count = orderService.countOrder(orderQueryParams);

        // 分頁
        PageUtil<Order> pageUtil = new PageUtil<>();
        pageUtil.setLimit(limit);
        pageUtil.setOffset(offset);
        pageUtil.setTotal(count);
        pageUtil.setResults(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);

    }

    @PostMapping("/members/{memberId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable Integer memberId,
                                         @RequestBody @Valid CreatedOrderRequest createdOrderRequest) {
        Integer orderId = orderService.createOrder(memberId, createdOrderRequest);

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);

    }

}
