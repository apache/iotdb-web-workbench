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
