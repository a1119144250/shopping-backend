package com.xiaowang.shopping.shop.order.convertor;

import cn.hutool.core.date.DatePattern;
import com.xiaowang.shopping.shop.order.domain.entity.Order;
import com.xiaowang.shopping.shop.order.domain.entity.OrderItem;
import com.xiaowang.shopping.shop.order.vo.CreateOrderResultVO;
import com.xiaowang.shopping.shop.order.vo.OrderAddressVO;
import com.xiaowang.shopping.shop.order.vo.OrderItemVO;
import com.xiaowang.shopping.shop.order.vo.OrderVO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单转换器
 * 
 * @author xiaowang
 */
public class OrderConvertor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

    /**
     * 转换为订单VO
     */
    public static OrderVO toOrderVO(Order order, List<OrderItem> orderItems, boolean includeEstimatedTime) {
        if (order == null) {
            return null;
        }

        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setStatus(getStatusCode(order.getStatus()));
        vo.setStatusText(getStatusText(order.getStatus()));

        // 转换订单项
        if (orderItems != null) {
            List<OrderItemVO> itemVOList = orderItems.stream()
                    .map(OrderConvertor::toOrderItemVO)
                    .collect(Collectors.toList());
            vo.setItems(itemVOList);
        }

        // 转换地址
        OrderAddressVO addressVO = new OrderAddressVO();
        addressVO.setName(order.getReceiverName());
        addressVO.setPhone(order.getReceiverPhone());
        addressVO.setRegion(order.getReceiverProvince() + " " + order.getReceiverCity() + " " + order.getReceiverDistrict());
        addressVO.setDetail(order.getReceiverAddress());
        vo.setAddress(addressVO);

        // 金额信息
        vo.setGoodsAmount(order.getTotalAmount().subtract(order.getFreightAmount()).add(order.getDiscountAmount()));
        vo.setDeliveryFee(order.getFreightAmount());
        vo.setCouponDiscount(order.getDiscountAmount());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setRemark(order.getRemark());

        // 时间信息
        vo.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().format(formatter) : null);
        vo.setPayTime(order.getPayTime() != null ? order.getPayTime().format(formatter) : null);
        vo.setDeliveryTime(order.getShipTime() != null ? order.getShipTime().format(formatter) : null);
        vo.setCompleteTime(order.getCompleteTime() != null ? order.getCompleteTime().format(formatter) : null);
        vo.setCancelTime(order.getCancelTime() != null ? order.getCancelTime().format(formatter) : null);

        // 预计送达时间（仅详情页）
        if (includeEstimatedTime && order.getStatus() >= 1 && order.getStatus() < 3) {
            vo.setEstimatedDeliveryTime("30-45分钟");
        }

        return vo;
    }

    /**
     * 转换为订单项VO
     */
    public static OrderItemVO toOrderItemVO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemVO vo = new OrderItemVO();
        vo.setProductId(orderItem.getProductId());
        vo.setProductName(orderItem.getProductName());
        vo.setProductImage(orderItem.getProductImage());
        vo.setPrice(orderItem.getProductPrice());
        vo.setCount(orderItem.getQuantity());
        vo.setSubtotal(orderItem.getTotalAmount());

        return vo;
    }

    /**
     * 构建创建订单结果VO
     */
    public static CreateOrderResultVO buildCreateOrderResultVO(Order order) {
        if (order == null) {
            return null;
        }

        CreateOrderResultVO vo = new CreateOrderResultVO();
        vo.setOrderId(String.valueOf(order.getId()));
        vo.setOrderNo(order.getOrderNo());

        return vo;
    }

    /**
     * 获取状态代码
     */
    private static String getStatusCode(Integer status) {
        if (status == null) {
            return "unknown";
        }

        switch (status) {
            case 0:
                return "pending";
            case 1:
                return "paid";
            case 2:
                return "preparing";
            case 3:
                return "completed";
            case 4:
                return "cancelled";
            default:
                return "unknown";
        }
    }

    /**
     * 获取状态文本
     */
    private static String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }

        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "已付款";
            case 2:
                return "配送中";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知";
        }
    }
}

