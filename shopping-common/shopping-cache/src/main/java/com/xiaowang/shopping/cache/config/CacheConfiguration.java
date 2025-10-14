package com.xiaowang.shopping.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 *
 * @author cola
 */
@Configuration
@EnableMethodCache(basePackages = "com.xiaowang.shopping")
public class CacheConfiguration {
}
