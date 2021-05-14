## CAT监控

cat是性能监控组件，主要用于监控http的请求时间，成功，失败率。

接入步骤：

1、引入依赖

```xml
<!-- cat监控  -->
<dependency>
    <groupId>com.dianping.cat</groupId>
    <artifactId>cat-client</artifactId>
    <version>3.0.0</version>
</dependency>
```

## 2.配置增加过滤器

##### 2.1 spring-boot项目添加配置类

```java
import com.dianping.cat.servlet.CatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * cat监控过滤器配置
 *
 */

@Configuration
public class CatFilterConfig {

    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatFilter filter = new CatFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);
        return registration;
    }

}
```
#####2.2 spring-mvc项目添加配置类
在web.xml中添加以下过滤器
```xml
<filter> 
    <filter-name>cat-filter</filter-name> 
    <filter-class>com.dianping.cat.servlet.CatFilter</filter-class> 
  </filter> 
  <filter-mapping> 
    <filter-name>cat-filter</filter-name> 
    <url-pattern>/*</url-pattern> 
    <dispatcher>REQUEST</dispatcher> 
    <dispatcher>FORWARD</dispatcher> 
  </filter-mapping> 
```

##3.创建配置文件

######src/main/resources/META-INF/app.properties
添加以下内容：
```xml
app.name={your app name}
```


## 注意事项：

>1、集成cat后，项目启动会变慢，建议在开发时不集成cat（即删除项目中的依赖和CatFilterConfig配置类），在服务器部署时集成cat。

>2、如果执行了上述步骤的1、2点，但是第3点没执行完或者执行错误，程序会报cat相关的错误，可以忽略，不影响整个项目的运行。

>3、步骤3中的/data/appdatas/cat文件路径无法改变。

>4、上述步骤3中的client.xml需要改ip为cat服务端的ip，http端口为cat服务端的端口。


## cat的简单使用

cat的访问地址是安装cat的ip+端口+/cat,如http://192.168.1.96:8080/cat。具体使用地址，请根据你的环境配置。


