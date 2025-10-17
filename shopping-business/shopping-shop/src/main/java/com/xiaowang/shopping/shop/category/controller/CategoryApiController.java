package com.xiaowang.shopping.shop.category.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.category.convertor.CategoryConvertor;
import com.xiaowang.shopping.shop.category.domain.entity.Category;
import com.xiaowang.shopping.shop.category.domain.service.CategoryService;
import com.xiaowang.shopping.shop.category.vo.CategoryVO;
import com.xiaowang.shopping.shop.common.vo.PageResult;
import com.xiaowang.shopping.shop.product.convertor.ProductConvertor;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import com.xiaowang.shopping.shop.product.vo.ProductListVO;
import com.xiaowang.shopping.web.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类接口控制器（新版API）
 * 
 * @author xiaowang
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * 获取所有商品分类
     */
    @GetMapping
    public Result<List<CategoryVO>> getCategories() {
        List<Category> categories = categoryService.listAllWithProductCount();
        
        // 转换为VO
        List<CategoryVO> voList = categories.stream()
                .map(CategoryConvertor::toCategoryVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    /**
     * 获取指定分类下的商品列表
     */
    @GetMapping("/{categoryId}/products")
    public Result<PageResult<ProductListVO>> getCategoryProducts(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 查询分类信息
        Category category = categoryService.getById(categoryId);
        
        // 查询该分类下的商品
        Page<Product> productPage = productService.pageWithSort(
                categoryId,
                null, // 不指定排序字段，使用默认排序
                "desc",
                page,
                pageSize
        );

        // 转换为VO
        List<ProductListVO> voList = productPage.getRecords().stream()
                .map(product -> ProductConvertor.toProductListVO(product, category.getName()))
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<ProductListVO> pageResult = new PageResult<>(
                productPage.getTotal(),
                page,
                pageSize,
                voList
        );

        return Result.success(pageResult);
    }
}

