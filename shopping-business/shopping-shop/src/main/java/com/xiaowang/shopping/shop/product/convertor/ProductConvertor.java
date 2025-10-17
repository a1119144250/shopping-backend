package com.xiaowang.shopping.shop.product.convertor;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.vo.NutritionVO;
import com.xiaowang.shopping.shop.product.vo.ProductDetailVO;
import com.xiaowang.shopping.shop.product.vo.ProductListVO;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 商品实体转换器
 * 
 * @author xiaowang
 */
@Slf4j
public class ProductConvertor {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

    /**
     * 转换为商品列表VO
     */
    public static ProductListVO toProductListVO(Product product, String categoryName) {
        if (product == null) {
            return null;
        }

        ProductListVO vo = new ProductListVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setImage(product.getMainImage());
        vo.setImages(parseJsonToList(product.getImages()));
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setDescription(product.getDescription());
        vo.setDetail(product.getDetail());
        vo.setSales(product.getSales());
        vo.setRating(product.getRating());
        vo.setStock(product.getStock());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(categoryName);
        vo.setStatus(product.getStatus());
        vo.setCreateTime(product.getCreateTime() != null ? product.getCreateTime().format(formatter) : null);

        return vo;
    }

    /**
     * 转换为商品详情VO
     */
    public static ProductDetailVO toProductDetailVO(Product product, String categoryName) {
        if (product == null) {
            return null;
        }

        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setImage(product.getMainImage());
        vo.setImages(parseJsonToList(product.getImages()));
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setDescription(product.getDescription());
        vo.setDetail(product.getDetail());
        vo.setSales(product.getSales());
        vo.setRating(product.getRating());
        vo.setStock(product.getStock());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(categoryName);
        vo.setIngredients(parseJsonToList(product.getIngredients()));
        vo.setNutrition(parseJsonToNutrition(product.getNutrition()));
        vo.setStatus(product.getStatus());
        vo.setCreateTime(product.getCreateTime() != null ? product.getCreateTime().format(formatter) : null);

        return vo;
    }

    /**
     * 解析JSON字符串为List
     */
    private static List<String> parseJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 尝试作为JSON数组解析
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 如果解析失败,检查是否是单个URL字符串
            String trimmed = json.trim();
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                // 将单个URL字符串包装成列表
                log.debug("将单个URL字符串转换为列表: {}", trimmed);
                return Collections.singletonList(trimmed);
            }
            
            log.error("解析JSON失败: {}", json, e);
            return Collections.emptyList();
        }
    }

    /**
     * 解析JSON字符串为营养成分对象
     */
    private static NutritionVO parseJsonToNutrition(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, NutritionVO.class);
        } catch (Exception e) {
            log.error("解析营养成分JSON失败: {}", json, e);
            return null;
        }
    }
}

