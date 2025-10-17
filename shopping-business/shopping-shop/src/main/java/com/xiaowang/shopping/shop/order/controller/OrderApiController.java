package com.xiaowang.shopping.shop.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.api.user.dto.AddressDTO;
import com.xiaowang.shopping.api.user.service.AddressFacadeService;
import com.xiaowang.shopping.shop.common.vo.PageResult;
import com.xiaowang.shopping.shop.order.convertor.OrderConvertor;
import com.xiaowang.shopping.shop.order.domain.entity.Order;
import com.xiaowang.shopping.shop.order.domain.entity.OrderItem;
import com.xiaowang.shopping.shop.order.domain.service.OrderService;
import com.xiaowang.shopping.shop.order.param.CancelOrderRequest;
import com.xiaowang.shopping.shop.order.param.CreateOrderRequest;
import com.xiaowang.shopping.shop.order.param.PayOrderRequest;
import com.xiaowang.shopping.shop.order.vo.CreateOrderResultVO;
import com.xiaowang.shopping.shop.order.vo.OrderVO;
import com.xiaowang.shopping.shop.order.vo.PayResultVO;
import com.xiaowang.shopping.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单接口控制器（新版API）
 * 
 * @author xiaowang
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    /**
     * 通过 Dubbo 调用 shopping-user 模块的地址服务
     */
    @DubboReference
    private AddressFacadeService addressFacadeService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<CreateOrderResultVO> createOrder(@RequestBody CreateOrderRequest request) {
        // 参数校验
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Result.error("1001", "订单商品不能为空");
        }
        if (request.getAddressId() == null) {
            return Result.error("1002", "收货地址不能为空");
        }

        Long userId = getUserId();

        // 通过 Dubbo 调用 shopping-user 模块获取地址信息
        AddressDTO address = addressFacadeService.getByIdAndUserId(request.getAddressId(), userId);
        if (address == null) {
            return Result.error("1003", "收货地址不存在");
        }

        // 拆分地区信息（region 格式："北京市 朝阳区" 或 "北京市 朝阳区 三里屯街道"）
        String[] regionParts = parseRegion(address.getRegion());
        String receiverProvince = regionParts[0];
        String receiverCity = regionParts[1];
        String receiverDistrict = regionParts[2];

        // 创建订单
        String orderNo = orderService.createOrder(
                userId,
                null, // cartItemIds - 如果从购物车创建，需要传入购物车项ID列表
                address.getName(),
                address.getPhone(),
                receiverProvince,
                receiverCity,
                receiverDistrict,
                address.getDetail(),
                request.getRemark()
        );

        // 获取创建的订单
        Order order = orderService.getByOrderNo(orderNo);

        // 转换为VO
        CreateOrderResultVO resultVO = OrderConvertor.buildCreateOrderResultVO(order);

        log.info("订单创建成功, userId: {}, orderNo: {}", userId, orderNo);
        return Result.success(resultVO);
    }

    /**
     * 拆分地区信息
     * region 格式："北京市 朝阳区" 或 "北京市 朝阳区 三里屯街道"
     * 
     * @param region 地区字符串
     * @return [省, 市, 区]
     */
    private String[] parseRegion(String region) {
        String[] result = new String[]{"", "", ""};
        
        if (region == null || region.trim().isEmpty()) {
            return result;
        }

        String[] parts = region.trim().split("\\s+");
        
        if (parts.length >= 1) {
            result[0] = parts[0]; // 省
        }
        if (parts.length >= 2) {
            result[1] = parts[1]; // 市
        }
        if (parts.length >= 3) {
            result[2] = parts[2]; // 区/街道
        }

        return result;
    }

    /**
     * 获取订单列表
     */
    @GetMapping
    public Result<PageResult<OrderVO>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {

        Long userId = getUserId();

        // 转换状态参数
        Integer statusCode = convertStatusToCode(status);

        // 查询订单列表
        Page<Order> orderPage = orderService.pageByUserId(userId, statusCode, page, pageSize);

        // 获取所有订单的订单项
        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderService.getOrderItems(order.getId());
                    return OrderConvertor.toOrderVO(order, orderItems, false);
                })
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<OrderVO> pageResult = new PageResult<>(
                orderPage.getTotal(),
                page,
                pageSize,
                voList
        );

        return Result.success(pageResult);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getOrder(@PathVariable Long id) {
        Long userId = getUserId();

        // 查询订单
        Order order = orderService.getById(id);

        // 验证订单所属
        if (!order.getUserId().equals(userId)) {
            return Result.error("4003", "无权访问该订单");
        }

        // 查询订单项
        List<OrderItem> orderItems = orderService.getOrderItems(id);

        // 转换为VO（包含预计送达时间）
        OrderVO vo = OrderConvertor.toOrderVO(order, orderItems, true);

        return Result.success(vo);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestBody(required = false) CancelOrderRequest request) {
        Long userId = getUserId();

        // 查询订单
        Order order = orderService.getById(id);

        // 验证订单所属
        if (!order.getUserId().equals(userId)) {
            return Result.error("4003", "无权访问该订单");
        }

        // 取消订单
        String reason = request != null ? request.getReason() : null;
        orderService.cancel(userId, order.getOrderNo(), reason);

        return Result.success(null);
    }

    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public Result<PayResultVO> payOrder(@PathVariable Long id, @RequestBody PayOrderRequest request) {
        Long userId = getUserId();

        // 参数校验
        if (request.getPayType() == null) {
            return Result.error("1001", "支付方式不能为空");
        }

        // 查询订单
        Order order = orderService.getById(id);

        // 验证订单所属
        if (!order.getUserId().equals(userId)) {
            return Result.error("4003", "无权访问该订单");
        }

        // 转换支付方式
        Integer payType = "wechat".equals(request.getPayType()) ? 1 : 2;

        // 支付订单
        orderService.pay(order.getOrderNo(), payType);

        // 构建支付结果（这里简化处理，实际应该调用支付服务）
        PayResultVO resultVO = new PayResultVO();
        Map<String, String> payInfo = new HashMap<>();
        payInfo.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        payInfo.put("nonceStr", "mock_nonce_str");
        payInfo.put("package", "prepay_id=mock_prepay_id");
        payInfo.put("signType", "RSA");
        payInfo.put("paySign", "mock_pay_sign");
        resultVO.setPayInfo(payInfo);

        return Result.success(resultVO);
    }

    /**
     * 确认收货
     */
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        Long userId = getUserId();

        // 查询订单
        Order order = orderService.getById(id);

        // 验证订单所属
        if (!order.getUserId().equals(userId)) {
            return Result.error("4003", "无权访问该订单");
        }

        // 确认收货
        orderService.confirm(userId, order.getOrderNo());

        return Result.success(null);
    }

    /**
     * 转换状态字符串为状态码
     */
    private Integer convertStatusToCode(String status) {
        if (status == null || "all".equals(status)) {
            return null;
        }

        switch (status) {
            case "pending":
                return 0;
            case "paid":
                return 1;
            case "preparing":
                return 2;
            case "completed":
                return 3;
            case "cancelled":
                return 4;
            default:
                return null;
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId() {
        String userIdStr = (String) StpUtil.getLoginId();
        return Long.valueOf(userIdStr);
    }
}

