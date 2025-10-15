package com.xiaowang.shopping.satoken.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 * 业务服务不需要Sa-Token的过滤器，因为鉴权已在网关完成
 * @author wangjin
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器，但不做任何鉴权
     * 只用于从请求中读取token信息
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 业务服务只需要能读取token，不需要鉴权（网关已鉴权）
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}

