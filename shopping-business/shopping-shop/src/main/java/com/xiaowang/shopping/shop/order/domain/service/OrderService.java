package com.xiaowang.shopping.shop.order.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.order.domain.entity.Order;
import com.xiaowang.shopping.shop.order.domain.entity.OrderItem;

import java.util.List;

/**
 * 订单服务接口
 * 
 * @author xiaowang
 */
public interface OrderService {

    /**
     * 创建订单
     */
    String createOrder(Long userId, List<Long> cartItemIds, String receiverName, 
                       String receiverPhone, String receiverProvince, String receiverCity,
                       String receiverDistrict, String receiverAddress, String remark);

    /**
     * 根据订单号查询订单
     */
    Order getByOrderNo(String orderNo);

    /**
     * 根据ID查询订单
     */
    Order getById(Long orderId);

    /**
     * 获取订单详情（包含订单项）
     */
    Order getOrderDetail(Long orderId);

    /**
     * 获取订单项列表
     */
    List<OrderItem> getOrderItems(Long orderId);

    /**
     * 分页查询用户订单列表
     */
    Page<Order> pageByUserId(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 支付订单
     */
    void pay(String orderNo, Integer payType);

    /**
     * 取消订单
     */
    void cancel(Long userId, String orderNo, String reason);

    /**
     * 确认收货
     */
    void confirm(Long userId, String orderNo);

    /**
     * 删除订单
     */
    void delete(Long userId, String orderNo);

    /**
     * 发货
     */
    void ship(String orderNo);

    /**
     * 自动取消超时未支付订单
     */
    void autoCancelTimeoutOrders();
}

