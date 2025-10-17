package com.xiaowang.shopping.api.user.service;

import com.xiaowang.shopping.api.user.dto.AddressDTO;

/**
 * 地址外观服务接口（Dubbo）
 * 
 * @author xiaowang
 */
public interface AddressFacadeService {

  /**
   * 根据地址ID和用户ID获取地址
   * 
   * @param addressId 地址ID
   * @param userId    用户ID
   * @return 地址信息
   */
  AddressDTO getByIdAndUserId(Long addressId, Long userId);

  /**
   * 获取用户的默认地址
   * 
   * @param userId 用户ID
   * @return 默认地址
   */
  AddressDTO getDefaultAddress(Long userId);
}
