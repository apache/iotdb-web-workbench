package org.apache.iotdb.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Profile 注解 标识加载在dev和test文件使用,在开发环境和测试环境的时候加载该类，线上生产环境为安全不建议创建swagger的bean
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .enable(true)
            .apiInfo(apiInfo())
            .select()
            // 加载swagger 扫描包
            .apis(RequestHandlerSelectors.basePackage("cn.cisdigital.demo.controller"))//接口文档扫描的包路径
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("XXXXX接口文档")
            .description("XXXX接口文档描述")
            .version("1.0")
            .build();
    }
}
