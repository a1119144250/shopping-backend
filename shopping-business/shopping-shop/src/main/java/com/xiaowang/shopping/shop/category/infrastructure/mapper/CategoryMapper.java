package com.xiaowang.shopping.shop.category.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.category.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

