package com.xiaowang.shopping.shop.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.web.vo.Result;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * 
 * @author xiaowang
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    /**
     * 分页查询商品列表
     */
    @GetMapping("/page")
    public Result<Page<Product>> page(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.page(categoryId, keyword, status, pageNum, pageSize));
    }

    /**
     * 获取推荐商品
     */
    @GetMapping("/recommended")
    public Result<List<Product>> getRecommended(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.getRecommendedProducts(limit));
    }

    /**
     * 获取热卖商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.getHotProducts(limit));
    }

    /**
     * 获取新品
     */
    @GetMapping("/new")
    public Result<List<Product>> getNew(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.getNewProducts(limit));
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result<Long> create(@RequestBody Product product) {
        return Result.success(productService.create(product));
    }

    /**
     * 更新商品
     */
    @PutMapping
    public Result<Void> update(@RequestBody Product product) {
        productService.update(product);
        return Result.success(null);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success(null);
    }

    /**
     * 上架商品
     */
    @PutMapping("/{id}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long id) {
        productService.onShelf(id);
        return Result.success(null);
    }

    /**
     * 下架商品
     */
    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        productService.offShelf(id);
        return Result.success(null);
    }
}

