<!--

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

-->

# swagger的使用

swagger在前后端联调开发中，启动非常重要的作用，如下：

1. 保持接口和文档的一致性
2. 使注释更为丰富



1、框架依赖中已包含swagger接口组件。
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.7.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

## 网页地址

你可以通过 http://localhost:9090/api/swagger-ui.html 这个地址获得接口信息。

## 生产环境、测试环境、开发环境配置

在生产环境中需要把swagger去掉，请参考配置类Swagger2Config中的@Profile({"dev", "test"})注解。（当前配置表示加载在dev和test文件使用,只在开发环境和测试环境的时候加载该类）