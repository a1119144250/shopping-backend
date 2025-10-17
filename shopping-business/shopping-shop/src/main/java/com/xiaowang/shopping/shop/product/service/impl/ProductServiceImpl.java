package com.xiaowang.shopping.shop.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.common.enums.ProductStatus;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopErrorCode;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopException;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import com.xiaowang.shopping.shop.product.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品服务实现
 * 
 * @author xiaowang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ShopException(ShopErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    @Override
    public Page<Product> page(Long categoryId, String keyword, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(status != null, Product::getStatus, status)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Product::getName, keyword)
                        .or()
                        .like(Product::getDescription, keyword))
                .orderByDesc(Product::getSort)
                .orderByDesc(Product::getCreateTime);
        
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Page<Product> pageWithSort(Long categoryId, String sortBy, String sortOrder, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 只查询上架的商品
        wrapper.eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .eq(categoryId != null, Product::getCategoryId, categoryId);
        
        // 根据排序字段排序
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        if ("sales".equals(sortBy)) {
            wrapper.orderBy(true, isAsc, Product::getSales);
        } else if ("price".equals(sortBy)) {
            wrapper.orderBy(true, isAsc, Product::getPrice);
        } else if ("rating".equals(sortBy)) {
            wrapper.orderBy(true, isAsc, Product::getRating);
        } else {
            // 默认按创建时间排序
            wrapper.orderByDesc(Product::getCreateTime);
        }
        
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Page<Product> search(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Product::getName, keyword)
                        .or()
                        .like(Product::getDescription, keyword))
                .orderByDesc(Product::getSales)
                .orderByDesc(Product::getCreateTime);
        
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Product> getRecommendedProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .eq(Product::getRecommended, 1)
                .orderByDesc(Product::getSort)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .eq(Product::getHot, 1)
                .orderByDesc(Product::getSales)
                .orderByDesc(Product::getSort)
                .last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    @Override
    public List<Product> getNewProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .eq(Product::getNewProduct, 1)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    @Override
    public Long create(Product product) {
        productMapper.insert(product);
        return product.getId();
    }

    @Override
    public void update(Product product) {
        Product existProduct = getById(product.getId());
        if (existProduct == null) {
            throw new ShopException(ShopErrorCode.PRODUCT_NOT_FOUND);
        }
        productMapper.updateById(product);
    }

    @Override
    public void delete(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new ShopException(ShopErrorCode.PRODUCT_NOT_FOUND);
        }
        productMapper.deleteById(id);
    }

    @Override
    public void onShelf(Long id) {
        Product product = getById(id);
        product.setStatus(ProductStatus.ON_SHELF.getCode());
        productMapper.updateById(product);
    }

    @Override
    public void offShelf(Long id) {
        Product product = getById(id);
        product.setStatus(ProductStatus.OFF_SHELF.getCode());
        productMapper.updateById(product);
    }

    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("stock = stock - " + quantity)
                .eq(Product::getId, productId)
                .ge(Product::getStock, quantity);
        int rows = productMapper.update(null, wrapper);
        return rows > 0;
    }

    @Override
    public void addStock(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("stock = stock + " + quantity)
                .eq(Product::getId, productId);
        productMapper.update(null, wrapper);
    }

    @Override
    public void addSales(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("sales = sales + " + quantity)
                .eq(Product::getId, productId);
        productMapper.update(null, wrapper);
    }
}

