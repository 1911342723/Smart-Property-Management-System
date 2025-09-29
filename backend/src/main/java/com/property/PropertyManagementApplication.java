package com.property;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 物业管理系统启动类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.property.mapper")
public class PropertyManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyManagementApplication.class, args);
        System.out.println("====================================");
        System.out.println("物业管理系统启动成功！");
        System.out.println("接口文档地址：http://localhost:8081/api/swagger-ui/index.html");
        System.out.println("Druid监控地址：http://localhost:8081/api/druid/index.html");
        System.out.println("====================================");
    }
}






