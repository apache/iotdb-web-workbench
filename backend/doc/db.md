## 数据库驱动、阿里巴巴连接池Durid、Mybatis-Plus

### 阿里巴巴连接池Durid

数据库连接池使用了阿里巴巴连接池Durid,有很多功能可以避免程序错误：

- 连接池维护，防止连接池内存泄露、提高连接速度
- 通过性能监控，发现数据库性能问题，提供慢sql日志等
- 放置sql注入
- 引入后性能不会下降
- 提供监控界面，非常完善的监控界面


如果不使用连接池，可以把下面的语句从pom.xml中去掉，但是你的程序，可能在连接数据库的使用变慢，也可能忘记释放连接池，从而使数据库出现问题，所以，我们不建议你去掉该jar包。

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
</dependency>
```

### 数据库访问库Mybatis-Plus

为减少数据库增删改查的代码量，引入了Mybatis-plus，官网文档：https://baomidou.com/，该库非常简单使用，
推荐一定要使用。对于单表的增删改查，基本不需要自己写代码，框架已经生成了根据各种条件查询数据库。 


首先引入mybatis-plus-boot-starter这个库：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.0</version>
</dependency>
```

如不使用，可以在pom.xml中将该依赖去掉。去掉该依赖后，还需要去掉相应的数据库类，建议保留。

Mybatis-plus的免费视频学习资料和代码demo，可以在[这里](https://mybatis.plus/guide/#%E4%BC%98%E7%A7%80%E8%A7%86%E9%A2%91)找到。

同时，建议大家把分页插件加上，这个包并不大，而且效率很高，建议如果使用数据库，90%以上的情况，请使用这个插件进行分页。


#### Mybatis-Plus分页插件

使用Mybatis-Plus分页需要配置分页插件，使用配置类的方式进行配置，具体可以参考config包下面的MybatisPlusConfig配置类。

1. 默认每页最多只能有100行，如需更改，请到MybatisPlusConfig内中更改。非常建议设置每页最多的条数，这样可以避免传入10000这种查询，导致数据库阻塞，应用程序内存泄露。

更多参考资料：https://mybatis.plus/guide/interceptor.html

分页代码如下：

```java
@GetMapping(value = "/list/user")
public BaseVO<Page<DemoUser>> listAllUser() {
    Page<DemoUser> page = new Page<>(1, 3);
    //查询年龄为1的demoUser 用户，并按照页面大小为3，当前页数为1的形式进行展示
    return BaseVO.success(demoUserService.lambdaQuery().eq(DemoUser::getAge, 1).page(page));
}
```
