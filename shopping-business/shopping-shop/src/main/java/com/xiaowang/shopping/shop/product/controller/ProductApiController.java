package com.xiaowang.shopping.shop.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.category.domain.entity.Category;
import com.xiaowang.shopping.shop.category.domain.service.CategoryService;
import com.xiaowang.shopping.shop.common.vo.PageResult;
import com.xiaowang.shopping.shop.product.convertor.ProductConvertor;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import com.xiaowang.shopping.shop.product.param.ProductQueryParam;
import com.xiaowang.shopping.shop.product.param.ProductSearchParam;
import com.xiaowang.shopping.shop.product.vo.ProductDetailVO;
import com.xiaowang.shopping.shop.product.vo.ProductListVO;
import com.xiaowang.shopping.web.vo.Result;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品接口控制器（新版API）
 * 
 * @author xiaowang
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * 获取商品列表（支持分页、分类过滤、排序）
     */
    @GetMapping
    public Result<PageResult<ProductListVO>> getProducts(ProductQueryParam param) {
        // 查询商品列表
        Page<Product> productPage = productService.pageWithSort(
                param.getCategoryId(),
                param.getSortBy(),
                param.getSortOrder(),
                param.getPage(),
                param.getPageSize()
        );

        // 获取所有分类信息
        List<Category> categories = categoryService.listAll();
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 转换为VO
        List<ProductListVO> voList = productPage.getRecords().stream()
                .map(product -> {
                    String categoryName = categoryMap.get(product.getCategoryId());
                    return ProductConvertor.toProductListVO(product, categoryName);
                })
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<ProductListVO> pageResult = new PageResult<>(
                productPage.getTotal(),
                param.getPage(),
                param.getPageSize(),
                voList
        );

        return Result.success(pageResult);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        // 查询商品
        Product product = productService.getById(id);

        // 查询分类名称
        String categoryName = null;
        if (product.getCategoryId() != null) {
            Category category = categoryService.getById(product.getCategoryId());
            categoryName = category.getName();
        }

        // 转换为VO
        ProductDetailVO vo = ProductConvertor.toProductDetailVO(product, categoryName);

        return Result.success(vo);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<PageResult<ProductListVO>> searchProducts(ProductSearchParam param) {
        // 验证关键词
        if (StringUtils.isBlank(param.getKeyword())) {
            return Result.error("1001", "搜索关键词不能为空");
        }

        // 搜索商品
        Page<Product> productPage = productService.search(
                param.getKeyword(),
                param.getPage(),
                param.getPageSize()
        );

        // 获取所有分类信息
        List<Category> categories = categoryService.listAll();
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 转换为VO
        List<ProductListVO> voList = productPage.getRecords().stream()
                .map(product -> {
                    String categoryName = categoryMap.get(product.getCategoryId());
                    return ProductConvertor.toProductListVO(product, categoryName);
                })
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<ProductListVO> pageResult = new PageResult<>(
                productPage.getTotal(),
                param.getPage(),
                param.getPageSize(),
                voList
        );

        return Result.success(pageResult);
    }
}

