package com.xiaowang.shopping.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaowang.shopping.user.controller.convertor.AddressConvertor;
import com.xiaowang.shopping.user.domain.entity.Address;
import com.xiaowang.shopping.user.domain.resp.AddressVO;
import com.xiaowang.shopping.user.domain.service.AddressService;
import com.xiaowang.shopping.user.param.AddressCreateRequest;
import com.xiaowang.shopping.user.param.AddressUpdateRequest;
import com.xiaowang.shopping.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地址管理接口
 * 
 * @author wangjin
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("address")
public class AddressController {

  private final AddressService addressService;

  /**
   * 获取地址列表
   * 
   * @return 地址列表
   */
  @GetMapping("/addresses")
  public Result<List<AddressVO>> getAddressList() {
    // 获取当前登录用户ID
    String userId = (String) StpUtil.getLoginId();

    // 查询地址列表
    List<Address> addressList = addressService.listByUserId(Long.valueOf(userId));

    // 转换为VO
    List<AddressVO> addressVOList = AddressConvertor.toAddressVOList(addressList);

    log.info("获取地址列表成功, userId: {}, count: {}", userId, addressVOList.size());
    return Result.success(addressVOList);
  }

  /**
   * 获取地址详情
   * 
   * @param id 地址ID
   * @return 地址详情
   */
  @GetMapping("/addresses/{id}")
  public Result<AddressVO> getAddressDetail(@PathVariable String id) {
    // 获取当前登录用户ID
    String userId = (String) StpUtil.getLoginId();

    // 查询地址详情
    Address address = addressService.getByIdAndUserId(Long.valueOf(id), Long.valueOf(userId));

    // 转换为VO
    AddressVO addressVO = AddressConvertor.toAddressVO(address);

    log.info("获取地址详情成功, userId: {}, addressId: {}", userId, id);
    return Result.success(addressVO);
  }

  /**
   * 创建地址
   * 
   * @param request 创建地址请求参数
   * @return 创建的地址信息
   */
  @PostMapping("/addresses")
  public Result<AddressVO> createAddress(@Valid @RequestBody AddressCreateRequest request) {
    // 获取当前登录用户ID
    String userId = (String) StpUtil.getLoginId();

    // 创建地址
    Address address = addressService.create(
        Long.valueOf(userId),
        request.getName(),
        request.getPhone(),
        request.getRegion(),
        request.getDetail(),
        request.getTag(),
        request.getIsDefault());

    // 转换为VO
    AddressVO addressVO = AddressConvertor.toAddressVO(address);

    log.info("创建地址成功, userId: {}, addressId: {}", userId, address.getId());
    return Result.success(addressVO);
  }

  /**
   * 更新地址
   * 
   * @param id      地址ID
   * @param request 更新地址请求参数
   * @return 更新后的地址信息
   */
  @PutMapping("/addresses/{id}")
  public Result<AddressVO> updateAddress(@PathVariable String id,
      @Valid @RequestBody AddressUpdateRequest request) {
    // 获取当前登录用户ID
    String userId = (String) StpUtil.getLoginId();

    // 更新地址
    Address address = addressService.update(
        Long.valueOf(id),
        Long.valueOf(userId),
        request.getName(),
        request.getPhone(),
        request.getRegion(),
        request.getDetail(),
        request.getTag(),
        request.getIsDefault());

    // 转换为VO
    AddressVO addressVO = AddressConvertor.toAddressVO(address);

    log.info("更新地址成功, userId: {}, addressId: {}", userId, id);
    return Result.success(addressVO);
  }

  /**
   * 删除地址
   * 
   * @param id 地址ID
   * @return 删除结果
   */
  @DeleteMapping("/addresses/{id}")
  public Result<Void> deleteAddress(@PathVariable String id) {
    // 获取当前登录用户ID
    String userId = (String) StpUtil.getLoginId();

    // 删除地址
    addressService.delete(Long.valueOf(id), Long.valueOf(userId));

    log.info("删除地址成功, userId: {}, addressId: {}", userId, id);
    return Result.success(null);
  }

  /**
   * 设置默认地址
   * 
   * @param id 地址ID
   * @return 更新后的地址信息
   */
  @PostMapping("/addresses/{id}/default")
  public Result<AddressVO> setDefaultAddress(@PathVariable String id) {
    // 获取当前登录用户ID
    String userId = (String) StpUtil.getLoginId();

    // 设置默认地址
    Address address = addressService.setDefault(Long.valueOf(id), Long.valueOf(userId));

    // 转换为VO
    AddressVO addressVO = AddressConvertor.toAddressVO(address);

    log.info("设置默认地址成功, userId: {}, addressId: {}", userId, id);
    return Result.success(addressVO);
  }
}
