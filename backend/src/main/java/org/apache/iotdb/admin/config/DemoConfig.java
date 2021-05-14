package org.apache.iotdb.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * apollo读取配置的测试类，本类为参考写法
 * 你可以删除本文件
 *
 * @author 辰行
 * @date 2020/09/25
 */

// 采用注解的形式注入命名空间，也可以在application.properties配置文件里面注入
// order是优先级，有多个空间的时候，根据order顺序读取，小的在前。
//@EnableApolloConfig(value = {"PAAS.redis"}, order = 1)
@Configuration
public class DemoConfig {

    /**
     * 读取PAAS.redis命名空间下key为redis.url的值，优先级低于默认命名空间，如果不存在，默认值为127.0.0.1:6371
     */
    @Value("${redis.url:127.0.0.1:6371}")
    private String url;

    public String getUrl() {
        return url;
    }


}
