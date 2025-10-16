package com.xiaowang.shopping.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.user.domain.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地址Mapper
 * 
 * @author wangjin
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {

  /**
   * 根据用户ID查询地址列表
   * 
   * @param userId 用户ID
   * @return 地址列表
   */
  List<Address> findByUserId(@Param("userId") Long userId);
}
