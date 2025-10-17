package com.xiaowang.shopping.shop.product.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}

