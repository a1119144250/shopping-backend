package com.xiaowang.shopping.shop.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaowang.shopping.shop.category.domain.entity.Category;
import com.xiaowang.shopping.shop.category.domain.service.CategoryService;
import com.xiaowang.shopping.shop.category.infrastructure.mapper.CategoryMapper;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopErrorCode;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopException;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务实现
 * 
 * @author xiaowang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ShopException(ShopErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    @Override
    public List<Category> listAll() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public List<Category> listAllWithProductCount() {
        List<Category> categories = listAll();
        // 为每个分类设置商品数量
        categories.forEach(category -> {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getCategoryId, category.getId())
                    .eq(Product::getStatus, 1); // 只统计上架的商品
            Long count = productMapper.selectCount(wrapper);
            category.setProductCount(count.intValue());
        });
        return categories;
    }

    @Override
    public List<Category> listByParentId(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public List<Category> getTree() {
        // 获取所有一级分类
        return listByParentId(0L);
    }

    @Override
    public Long create(Category category) {
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void update(Category category) {
        Category existCategory = getById(category.getId());
        if (existCategory == null) {
            throw new ShopException(ShopErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new ShopException(ShopErrorCode.CATEGORY_NOT_FOUND);
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        Long count = categoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ShopException(ShopErrorCode.CATEGORY_HAS_CHILDREN);
        }
        
        categoryMapper.deleteById(id);
    }
}

