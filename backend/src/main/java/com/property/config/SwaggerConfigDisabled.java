package com.property.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger禁用配置
 * 如果Swagger有兼容性问题，可以通过设置 swagger.enabled=false 来禁用
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "false")
public class SwaggerConfigDisabled {
    // 空配置类，用于禁用Swagger
}






