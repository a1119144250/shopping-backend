package com.xiaowang.shopping.shop.category.controller;

import com.xiaowang.shopping.web.vo.Result;
import com.xiaowang.shopping.shop.category.domain.entity.Category;
import com.xiaowang.shopping.shop.category.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 * 
 * @author xiaowang
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> listAll() {
        return Result.success(categoryService.listAll());
    }

    /**
     * 根据父ID获取子分类
     */
    @GetMapping("/list/{parentId}")
    public Result<List<Category>> listByParentId(@PathVariable Long parentId) {
        return Result.success(categoryService.listByParentId(parentId));
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<Category>> getTree() {
        return Result.success(categoryService.getTree());
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Long> create(@RequestBody Category category) {
        return Result.success(categoryService.create(category));
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success(null);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success(null);
    }
}

