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

# 配置中心 Apollo

##接入步骤
1. 添加依赖 ，在pom.xml 中添加依赖，如不需要apolo，可以去掉该依赖

```xml
<dependency>
  <groupId>com.ctrip.framework.apollo</groupId>
  <artifactId>apollo-client</artifactId>
  <version>1.2.0</version>
</dependency>
```

版本号请使用1.2.0，服务器版本xxx

2. 在application.properties文件中增加必要配置

```properties
    #配置中心服务地址
    apollo.meta=http://192.168.1.241:8081
    #开启配置中心
    apollo.bootstrap.enabled=true
    # 采用配置的形式注入命名空间application
    apollo.bootstrap.namespaces=application
```

3. 编写读取配置的类。参考bean包下面的TestBean类。
