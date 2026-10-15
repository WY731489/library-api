package com.lb.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 文档配置
 * 访问地址：http://localhost:8081/doc.html
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("图书借阅管理系统 API 文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot 3 + MyBatis-Plus + JWT 的图书借阅系统")
                        .contact(new Contact().name("library").email("admin@library.com")));
    }
}
