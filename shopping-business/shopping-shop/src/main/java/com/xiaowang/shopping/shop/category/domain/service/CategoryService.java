package com.xiaowang.shopping.shop.category.domain.service;

import com.xiaowang.shopping.shop.category.domain.entity.Category;

import java.util.List;

/**
 * 商品分类服务接口
 * 
 * @author xiaowang
 */
public interface CategoryService {

    /**
     * 根据ID查询分类
     */
    Category getById(Long id);

    /**
     * 获取所有分类列表
     */
    List<Category> listAll();

    /**
     * 获取所有分类列表（包含商品数量）
     */
    List<Category> listAllWithProductCount();

    /**
     * 根据父ID获取子分类列表
     */
    List<Category> listByParentId(Long parentId);

    /**
     * 获取分类树
     */
    List<Category> getTree();

    /**
     * 创建分类
     */
    Long create(Category category);

    /**
     * 更新分类
     */
    void update(Category category);

    /**
     * 删除分类
     */
    void delete(Long id);
}

