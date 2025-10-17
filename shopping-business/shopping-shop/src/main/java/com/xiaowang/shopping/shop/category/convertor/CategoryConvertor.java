package com.xiaowang.shopping.shop.category.convertor;

import com.xiaowang.shopping.shop.category.domain.entity.Category;
import com.xiaowang.shopping.shop.category.vo.CategoryVO;

/**
 * 分类实体转换器
 * 
 * @author xiaowang
 */
public class CategoryConvertor {

    /**
     * 转换为分类VO
     */
    public static CategoryVO toCategoryVO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setIcon(category.getIcon());
        vo.setDescription(category.getDescription());
        vo.setSort(category.getSort());
        vo.setProductCount(category.getProductCount() != null ? category.getProductCount() : 0);
        vo.setStatus(category.getStatus());

        return vo;
    }
}

