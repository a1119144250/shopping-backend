package com.xiaowang.shopping.shop.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.web.vo.Result;
import com.xiaowang.shopping.shop.order.domain.entity.Order;
import com.xiaowang.shopping.shop.order.domain.entity.OrderItem;
import com.xiaowang.shopping.shop.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 
 * @author xiaowang
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<String> createOrder(
            @RequestParam Long userId,
            @RequestBody List<Long> cartItemIds,
            @RequestParam String receiverName,
            @RequestParam String receiverPhone,
            @RequestParam String receiverProvince,
            @RequestParam String receiverCity,
            @RequestParam String receiverDistrict,
            @RequestParam String receiverAddress,
            @RequestParam(required = false) String remark) {
        String orderNo = orderService.createOrder(userId, cartItemIds, receiverName, receiverPhone,
                receiverProvince, receiverCity, receiverDistrict, receiverAddress, remark);
        return Result.success(orderNo);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getById(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(orderId));
    }

    /**
     * 根据订单号获取订单
     */
    @GetMapping("/no/{orderNo}")
    public Result<Order> getByOrderNo(@PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    /**
     * 获取订单项列表
     */
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderItems(orderId));
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping("/page")
    public Result<Page<Order>> page(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.pageByUserId(userId, status, pageNum, pageSize));
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay")
    public Result<Void> pay(
            @RequestParam String orderNo,
            @RequestParam Integer payType) {
        orderService.pay(orderNo, payType);
        return Result.success(null);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public Result<Void> cancel(
            @RequestParam Long userId,
            @RequestParam String orderNo,
            @RequestParam(required = false) String reason) {
        orderService.cancel(userId, orderNo, reason);
        return Result.success(null);
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm")
    public Result<Void> confirm(
            @RequestParam Long userId,
            @RequestParam String orderNo) {
        orderService.confirm(userId, orderNo);
        return Result.success(null);
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{orderNo}")
    public Result<Void> delete(
            @RequestParam Long userId,
            @PathVariable String orderNo) {
        orderService.delete(userId, orderNo);
        return Result.success(null);
    }

    /**
     * 发货（管理员操作）
     */
    @PostMapping("/ship")
    public Result<Void> ship(@RequestParam String orderNo) {
        orderService.ship(orderNo);
        return Result.success(null);
    }
}

