package com.xiaowang.shopping.user.facade;

import com.xiaowang.shopping.api.user.dto.AddressDTO;
import com.xiaowang.shopping.api.user.service.AddressFacadeService;
import com.xiaowang.shopping.user.domain.entity.Address;
import com.xiaowang.shopping.user.domain.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 地址外观服务实现（Dubbo）
 * 
 * @author xiaowang
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class AddressFacadeServiceImpl implements AddressFacadeService {

  private final AddressService addressService;

  @Override
  public AddressDTO getByIdAndUserId(Long addressId, Long userId) {
    Address address = addressService.getByIdAndUserId(addressId, userId);
    return convertToDTO(address);
  }

  @Override
  public AddressDTO getDefaultAddress(Long userId) {
    // 获取用户的所有地址
    var addresses = addressService.listByUserId(userId);

    // 找到默认地址
    Address defaultAddress = addresses.stream()
        .filter(addr -> addr.getIsDefault() != null && addr.getIsDefault() == 1)
        .findFirst()
        .orElse(null);

    if (defaultAddress == null && !addresses.isEmpty()) {
      // 如果没有默认地址，返回第一个
      defaultAddress = addresses.get(0);
    }

    return convertToDTO(defaultAddress);
  }

  /**
   * 转换为DTO
   */
  private AddressDTO convertToDTO(Address address) {
    if (address == null) {
      return null;
    }

    AddressDTO dto = new AddressDTO();
    dto.setId(address.getId());
    dto.setUserId(address.getUserId());
    dto.setName(address.getName());
    dto.setPhone(address.getPhone());
    dto.setRegion(address.getRegion());
    dto.setDetail(address.getDetail());
    dto.setTag(address.getTag());
    dto.setIsDefault(address.getIsDefault());

    return dto;
  }
}
