# IotDB admin快速入门


该项目IotDB admin，IotDB可视化监控程序。


## 支持的环境

目前支持的开发环境如下：

- Java 1.8
- Maven 3.3.9 或以上
- Gradle
- 推荐IntelliJ IDEA 2017

## 目录结构说明

doc : 文档目录，一些帮助文档
sql : demo项目的sql语句，直接导入mysql就可以了。
src : 源码文件
dockerfile : docker镜像打包文件
pom.xml : Maven Pom文件
README.md : 帮助文件目录

## 使用的公用库

由于是开源软件，使用的所有库都应该是maven公有仓库能找的，满足轻量、稳定2个特性。


## 快速启动

项目提供maven、gradle两种构建工具,以maven为例启动项目。
首先右键点击pom.xml,点击import maven project导入项目相关依赖,完成后启动AdminApplication运行项目
默认登录用户为root,密码为123456。

