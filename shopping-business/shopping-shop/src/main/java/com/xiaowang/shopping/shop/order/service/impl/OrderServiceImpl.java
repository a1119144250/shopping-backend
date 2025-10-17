package com.xiaowang.shopping.shop.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import com.xiaowang.shopping.shop.cart.domain.service.CartService;
import com.xiaowang.shopping.shop.common.enums.OrderStatus;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopErrorCode;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopException;
import com.xiaowang.shopping.shop.order.domain.entity.Order;
import com.xiaowang.shopping.shop.order.domain.entity.OrderItem;
import com.xiaowang.shopping.shop.order.domain.service.OrderService;
import com.xiaowang.shopping.shop.order.infrastructure.mapper.OrderItemMapper;
import com.xiaowang.shopping.shop.order.infrastructure.mapper.OrderMapper;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 * 
 * @author xiaowang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartService cartService;
    private final ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, List<Long> cartItemIds, String receiverName,
                              String receiverPhone, String receiverProvince, String receiverCity,
                              String receiverDistrict, String receiverAddress, String remark) {
        // 获取购物车项
        List<CartItem> cartItems = cartService.getSelectedItems(userId);
        if (cartItems.isEmpty()) {
            throw new ShopException("请选择要购买的商品", ShopErrorCode.CART_ITEM_NOT_FOUND);
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotal = cartItem.getProductPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverProvince(receiverProvince);
        order.setReceiverCity(receiverCity);
        order.setReceiverDistrict(receiverDistrict);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(remark);
        orderMapper.insert(order);

        // 创建订单项
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductImage(cartItem.getProductImage());
            orderItem.setProductPrice(cartItem.getProductPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalAmount(cartItem.getProductPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItemMapper.insert(orderItem);

            // 扣减库存
            boolean success = productService.deductStock(cartItem.getProductId(), cartItem.getQuantity());
            if (!success) {
                throw new ShopException("商品【" + cartItem.getProductName() + "】库存不足",
                        ShopErrorCode.PRODUCT_STOCK_INSUFFICIENT);
            }
        }

        // 删除购物车项
        cartService.deleteItems(userId, cartItemIds);

        return orderNo;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new ShopException(ShopErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public Order getById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ShopException(ShopErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        Order order = getById(orderId);
        // 这里可以扩展，加载订单项等详细信息
        return order;
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId)
                .orderByAsc(OrderItem::getId);
        return orderItemMapper.selectList(wrapper);
    }

    @Override
    public Page<Order> pageByUserId(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(String orderNo, Integer payType) {
        Order order = getByOrderNo(orderNo);
        
        // 检查订单状态
        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            throw new ShopException("订单状态不正确", ShopErrorCode.ORDER_CANNOT_PAY);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 增加销量
        List<OrderItem> orderItems = getOrderItems(order.getId());
        for (OrderItem orderItem : orderItems) {
            productService.addSales(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, String orderNo, String reason) {
        Order order = getByOrderNo(orderNo);
        
        // 校验用户权限
        if (!order.getUserId().equals(userId)) {
            throw new ShopException(ShopErrorCode.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            throw new ShopException("订单已支付，无法取消", ShopErrorCode.ORDER_CANNOT_CANCEL);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> orderItems = getOrderItems(order.getId());
        for (OrderItem orderItem : orderItems) {
            productService.addStock(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long userId, String orderNo) {
        Order order = getByOrderNo(orderNo);
        
        // 校验用户权限
        if (!order.getUserId().equals(userId)) {
            throw new ShopException(ShopErrorCode.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (!OrderStatus.SHIPPED.getCode().equals(order.getStatus())) {
            throw new ShopException("订单状态不正确", ShopErrorCode.ORDER_STATUS_ERROR);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void delete(Long userId, String orderNo) {
        Order order = getByOrderNo(orderNo);
        
        // 校验用户权限
        if (!order.getUserId().equals(userId)) {
            throw new ShopException(ShopErrorCode.ORDER_NOT_FOUND);
        }

        // 只能删除已完成或已取消的订单
        if (!OrderStatus.COMPLETED.getCode().equals(order.getStatus()) 
                && !OrderStatus.CANCELLED.getCode().equals(order.getStatus())) {
            throw new ShopException("只能删除已完成或已取消的订单", ShopErrorCode.ORDER_STATUS_ERROR);
        }

        orderMapper.deleteById(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(String orderNo) {
        Order order = getByOrderNo(orderNo);
        
        // 检查订单状态（只能发货已支付的订单）
        if (!OrderStatus.PAID.getCode().equals(order.getStatus())) {
            throw new ShopException("订单未支付", ShopErrorCode.ORDER_STATUS_ERROR);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.SHIPPED.getCode());
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCancelTimeoutOrders() {
        // 查询超时未支付的订单（30分钟）
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, OrderStatus.PENDING_PAYMENT.getCode())
                .lt(Order::getCreateTime, timeoutTime);
        
        List<Order> timeoutOrders = orderMapper.selectList(wrapper);
        for (Order order : timeoutOrders) {
            // 取消订单
            order.setStatus(OrderStatus.CLOSED.getCode());
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("订单超时自动取消");
            orderMapper.updateById(order);

            // 恢复库存
            List<OrderItem> orderItems = getOrderItems(order.getId());
            for (OrderItem orderItem : orderItems) {
                productService.addStock(orderItem.getProductId(), orderItem.getQuantity());
            }
        }
        
        if (!timeoutOrders.isEmpty()) {
            log.info("自动取消超时订单数量: {}", timeoutOrders.size());
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 格式：时间戳 + UUID后6位
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return timestamp + uuid;
    }
}

