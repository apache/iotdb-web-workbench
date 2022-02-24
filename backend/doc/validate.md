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

# 参数验证

参数验证一般有很多种方法：

1. 直接写在controller中，有时候，特别是新人，98%会忘记写参数验证
2. 切面，注解形式的参数验证

我们认为第2种方式会好于第一种方式，所以我们使用了成熟的hibernate验证框架对参数进行验证

## hibernate参数验证配置

在开发中经常需要写一些字段校验的代码，比如字段非空，字段长度限制，邮箱格式验证等等，写这些与业务逻辑关系不大的代码有几个麻烦：

1. 验证代码繁琐，重复劳动
2. 方法内代码显得冗长
3. 每次要看哪些参数验证是否完整，需要去翻阅验证逻辑代码
  
[hibernate validator](http://hibernate.org/validator/documentation/) 提供了一套比较完善、便捷的验证实现方式。

spring-boot-starter-web包里面有hibernate-validator包，不需要引用hibernate validator依赖。

## 校验框架的使用方式

接入步骤：

1、引入依赖

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.9.Final</version>
</dependency>
```

2、在需要校验的类上的属性上加上相关注解

如DemoUser中name上加@NotNull和@Length注解

3、进行aop配置，创建ParamValidAspect切面配置（可选）。


## 注意事项：

>如果不接入ParamValidAspect切面配置，校验框架会自动抛出Spring的参数校验的异常；如果使用了自定义切面配置，
可以在doAround方法中对相关的参数校验做特殊的处理。本项目中对参数校验进行了统一的特殊处理（切面配置），返回与
异常主见中的异常保持一致的错误状态码。建议以该项目的方式进行切面配置，从而遵循异常状态码规范。

