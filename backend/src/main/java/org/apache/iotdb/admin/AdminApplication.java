package org.apache.iotdb.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * 后端代码启动类
 * 1. MapperScan需要配置为mapper所在的包，自动扫描mapper，MapperScan是用于数据库组件自动扫描使用的。
 */
@SpringBootApplication
@MapperScan("org.apache.iotdb.admin.mapper")
@EnableWebSecurity
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
