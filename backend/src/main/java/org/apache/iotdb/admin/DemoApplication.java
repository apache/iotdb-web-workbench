package org.apache.iotdb.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * MapperScan需要配置为mapper所在的包，自动扫描mapper
 *
 * @author fanli
 */

@SpringBootApplication
@ComponentScan(value = {"org.apache.iotdb.admin"})
// Mybatis-plus
@MapperScan("org.apache.iotdb.admin.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
