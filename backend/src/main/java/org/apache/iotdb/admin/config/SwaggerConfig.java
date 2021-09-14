/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/** swagger配置类 */
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class SwaggerConfig {

  @Bean
  public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiinfo())
        .enable(true)
        .globalOperationParameters(jwtToken())
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.apache.iotdb.admin.controller"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiinfo() {
    return new ApiInfoBuilder()
        .title("IOTDB接口文档")
        .description("iotDB层级关系 存储组 -> 实体(设备) -> 测点(时间序列)")
        .build();
  }

  private List<Parameter> jwtToken() {

    ParameterBuilder tokenPar = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<>();
    tokenPar
        .name("Authorization")
        .description("jwt令牌")
        .modelRef(new ModelRef("string"))
        .parameterType("header")
        .required(false);
    pars.add(tokenPar.build());
    return pars;
  }
}
