package com.xiaowang.shopping.shop.product.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.product.domain.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 * 
 * @author xiaowang
 */
public interface ProductService {

    /**
     * 根据ID查询商品
     */
    Product getById(Long id);

    /**
     * 分页查询商品列表
     */
    Page<Product> page(Long categoryId, String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 分页查询商品列表（支持排序）
     */
    Page<Product> pageWithSort(Long categoryId, String sortBy, String sortOrder, Integer pageNum, Integer pageSize);

    /**
     * 搜索商品
     */
    Page<Product> search(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取推荐商品列表
     */
    List<Product> getRecommendedProducts(Integer limit);

    /**
     * 获取热卖商品列表
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 获取新品列表
     */
    List<Product> getNewProducts(Integer limit);

    /**
     * 创建商品
     */
    Long create(Product product);

    /**
     * 更新商品
     */
    void update(Product product);

    /**
     * 删除商品
     */
    void delete(Long id);

    /**
     * 上架商品
     */
    void onShelf(Long id);

    /**
     * 下架商品
     */
    void offShelf(Long id);

    /**
     * 扣减库存
     */
    boolean deductStock(Long productId, Integer quantity);

    /**
     * 增加库存
     */
    void addStock(Long productId, Integer quantity);

    /**
     * 增加销量
     */
    void addSales(Long productId, Integer quantity);
}

