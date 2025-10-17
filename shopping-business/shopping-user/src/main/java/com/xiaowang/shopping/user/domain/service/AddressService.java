package com.xiaowang.shopping.user.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xiaowang.shopping.user.domain.entity.Address;
import com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode;
import com.xiaowang.shopping.user.infrastructure.exception.UserException;
import com.xiaowang.shopping.user.infrastructure.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址服务
 * 
 * @author wangjin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

  private final AddressMapper addressMapper;

  /**
   * 根据用户ID查询地址列表
   * 
   * @param userId 用户ID
   * @return 地址列表
   */
  public List<Address> listByUserId(Long userId) {
    QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", userId)
        .orderByDesc("is_default") // 默认地址排在前面
        .orderByDesc("update_time"); // 按更新时间倒序

    return addressMapper.selectList(queryWrapper);
  }

  /**
   * 根据地址ID和用户ID查询地址详情
   * 
   * @param addressId 地址ID
   * @param userId    用户ID
   * @return 地址详情
   */
  public Address getByIdAndUserId(Long addressId, Long userId) {
    QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", addressId)
        .eq("user_id", userId);

    Address address = addressMapper.selectOne(queryWrapper);

    if (address == null) {
      throw new UserException(UserErrorCode.ADDRESS_NOT_EXIST);
    }

    return address;
  }

  /**
   * 创建地址
   * 
   * @param userId    用户ID
   * @param name      收货人姓名
   * @param phone     手机号
   * @param region    所在地区
   * @param detail    详细地址
   * @param tag       地址标签
   * @param isDefault 是否为默认地址
   * @return 创建的地址
   */
  @Transactional(rollbackFor = Exception.class)
  public Address create(Long userId, String name, String phone, String region, String detail, String tag,
      Boolean isDefault) {
    // 如果设置为默认地址，先将该用户的其他地址设置为非默认
    if (isDefault != null && isDefault) {
      clearDefaultAddress(userId);
    }

    // 创建新地址
    Address address = new Address();
    address.setUserId(userId);
    address.setName(name);
    address.setPhone(phone);
    address.setRegion(region);
    address.setDetail(detail);
    address.setTag(tag == null || tag.isEmpty() ? "家" : tag);
    address.setIsDefault(isDefault != null && isDefault ? 1 : 0);

    // 时间字段由 MyBatis Plus 自动填充

    // 保存到数据库
    addressMapper.insert(address);

    log.info("创建地址成功, userId: {}, addressId: {}", userId, address.getId());
    return address;
  }

  /**
   * 更新地址
   * 
   * @param addressId 地址ID
   * @param userId    用户ID
   * @param name      收货人姓名
   * @param phone     手机号
   * @param region    所在地区
   * @param detail    详细地址
   * @param tag       地址标签
   * @param isDefault 是否为默认地址
   * @return 更新后的地址
   */
  @Transactional(rollbackFor = Exception.class)
  public Address update(Long addressId, Long userId, String name, String phone, String region, String detail,
      String tag, Boolean isDefault) {
    // 先查询地址是否存在且属于当前用户
    Address existingAddress = getByIdAndUserId(addressId, userId);

    // 如果要设置为默认地址，先清除该用户的其他默认地址
    if (isDefault != null && isDefault) {
      clearDefaultAddress(userId);
    }

    // 更新地址信息
    existingAddress.setName(name);
    existingAddress.setPhone(phone);
    existingAddress.setRegion(region);
    existingAddress.setDetail(detail);
    existingAddress.setTag(tag == null || tag.isEmpty() ? "家" : tag);
    existingAddress.setIsDefault(isDefault != null && isDefault ? 1 : 0);
    // updateTime 由 MyBatis Plus 自动填充

    // 保存到数据库
    addressMapper.updateById(existingAddress);

    log.info("更新地址成功, userId: {}, addressId: {}", userId, addressId);
    return existingAddress;
  }

  /**
   * 删除地址
   * 
   * @param addressId 地址ID
   * @param userId    用户ID
   */
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long addressId, Long userId) {
    // 先查询地址是否存在且属于当前用户
    getByIdAndUserId(addressId, userId);

    // 删除地址（逻辑删除）
    addressMapper.deleteById(addressId);

    log.info("删除地址成功, userId: {}, addressId: {}", userId, addressId);
  }

  /**
   * 设置默认地址
   * 
   * @param addressId 地址ID
   * @param userId    用户ID
   * @return 更新后的地址
   */
  @Transactional(rollbackFor = Exception.class)
  public Address setDefault(Long addressId, Long userId) {
    // 先查询地址是否存在且属于当前用户
    Address address = getByIdAndUserId(addressId, userId);

    // 如果该地址已经是默认地址，直接返回
    if (address.getIsDefault() != null && address.getIsDefault() == 1) {
      log.info("地址已经是默认地址, userId: {}, addressId: {}", userId, addressId);
      return address;
    }

    // 清除该用户的其他默认地址
    clearDefaultAddress(userId);

    // 设置当前地址为默认地址
    address.setIsDefault(1);
    // updateTime 由 MyBatis Plus 自动填充
    addressMapper.updateById(address);

    log.info("设置默认地址成功, userId: {}, addressId: {}", userId, addressId);
    return address;
  }

  /**
   * 清除用户的默认地址
   * 
   * @param userId 用户ID
   */
  private void clearDefaultAddress(Long userId) {
    UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("user_id", userId)
        .eq("is_default", 1)
        .set("is_default", 0);
    // updateTime 由 MyBatis Plus 自动填充

    addressMapper.update(null, updateWrapper);
  }
}
