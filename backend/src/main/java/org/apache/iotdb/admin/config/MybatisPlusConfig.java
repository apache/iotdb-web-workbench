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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 该类是Mybatis-Plus的配置类，mybatis以插件的形式来实现特定功能 */
@Configuration
public class MybatisPlusConfig {

  /**
   * 分页插件配置，对相关数据库进行拦截并分页
   *
   * @return 分页拦截器
   */
  @Bean
  public MybatisPlusInterceptor paginationInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 使用不同的数据库，可以更改DbType.MYSQL的类型
    PaginationInnerInterceptor paginationInnerInterceptor =
        new PaginationInnerInterceptor(DbType.MYSQL);
    // 每页最多100条，可以更改，但不建议太大
    paginationInnerInterceptor.setMaxLimit(100L);
    interceptor.addInnerInterceptor(paginationInnerInterceptor);
    return interceptor;
  }
}
