package com.xiaowang.shopping.tcc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.tcc.entity.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cola
 * 事务日志
 */
@Mapper
public interface TransactionLogMapper extends BaseMapper<TransactionLog> {

}
