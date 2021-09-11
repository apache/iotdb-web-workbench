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

# IoTDB-Workbench快速入门

该项目IoTDB-Workbench，为IoTDB的可视化监控程序。

## 支持的环境

目前支持的开发环境如下：

- Java 1.8
- Maven 3.3.9 或以上
- Gradle
- 推荐IntelliJ IDEA 2017或以上

## 目录结构说明

- doc : 文档目录，一些帮助文档
- src : 源码文件
- Dockerfile : docker镜像打包文件
- pom.xml : Maven Pom文件
- README.md : 帮助文件目录

## 使用的公用库

由于是开源软件，使用的所有库都应该是maven公有仓库能找的，满足轻量、稳定2个特性。

## 快速启动

项目提供maven、gradle两种构建工具,以maven为例启动项目：  
首先通过IntelliJ IDEA打开本项目，右键点击pom.xml,点击Add as Maven Project导入项目相关依赖。
![](image/pom.PNG)  
导入完成后启动AdminApplication项目，如果未报错并出现如下界面则代表项目运行成功。  
![](image/启动成功.PNG)  
本系统默认登录用户为root,密码为123456。  
具体部署步骤请参考[部署文档](deploy.md)