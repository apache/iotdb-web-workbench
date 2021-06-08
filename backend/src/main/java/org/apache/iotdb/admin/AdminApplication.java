package org.apache.iotdb.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * MapperScan需要配置为mapper所在的包，自动扫描mapper
 */

@SpringBootApplication
@ComponentScan(value = {"org.apache.iotdb.admin"})
@MapperScan("org.apache.iotdb.admin.mapper")
@EnableWebSecurity
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
