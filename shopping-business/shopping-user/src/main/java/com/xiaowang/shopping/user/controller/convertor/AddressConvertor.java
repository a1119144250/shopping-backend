package com.xiaowang.shopping.user.controller.convertor;

import cn.hutool.core.date.DatePattern;
import com.xiaowang.shopping.user.domain.entity.Address;
import com.xiaowang.shopping.user.domain.resp.AddressVO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 地址转换器
 * 
 * @author wangjin
 */
public class AddressConvertor {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

  /**
   * 将Address实体转换为AddressVO
   * 
   * @param address 地址实体
   * @return AddressVO
   */
  public static AddressVO toAddressVO(Address address) {
    if (address == null) {
      return null;
    }

    AddressVO addressVO = new AddressVO();
    addressVO.setId(String.valueOf(address.getId()));
    addressVO.setName(address.getName());
    addressVO.setPhone(address.getPhone());
    addressVO.setRegion(address.getRegion());
    addressVO.setDetail(address.getDetail());
    addressVO.setTag(address.getTag());
    addressVO.setIsDefault(address.getIsDefault() != null && address.getIsDefault() == 1);
    addressVO.setCreateTime(address.getCreateTime() != null ? address.getCreateTime().format(formatter) : null);
    addressVO.setUpdateTime(address.getUpdateTime() != null ? address.getUpdateTime().format(formatter) : null);

    return addressVO;
  }

  /**
   * 批量转换Address实体列表为AddressVO列表
   * 
   * @param addressList 地址实体列表
   * @return AddressVO列表
   */
  public static List<AddressVO> toAddressVOList(List<Address> addressList) {
    if (addressList == null || addressList.isEmpty()) {
      return new ArrayList<>();
    }

    List<AddressVO> voList = new ArrayList<>();
    for (Address address : addressList) {
      voList.add(toAddressVO(address));
    }

    return voList;
  }
}
