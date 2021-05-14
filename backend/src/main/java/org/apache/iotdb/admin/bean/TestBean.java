package org.apache.iotdb.admin.bean;

import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 辰行
 * <p>
 * apollo读取配置的测试类
 * 已经在配置中注入了applicaion命名空间所以不需要@EnableApolloConfig注解
 * @date 2020/09/25
 */
@Component("testBean")
public class TestBean {

    /**
     * 读取默认命名空间application下key为application_name属性，如果没有，则为默认值test
     */
    @Value("${application_name:test}")
    private String applicationName;

    /**
     * 支持json的形式直接获取json解析后的对象
     */
    @ApolloJsonValue("${jsonBeanProperty:[]}")
    private List<JsonBean> jsonBeans;


    @Override
    public String toString() {
        return String.format("[TestBean] applicationName: %s, jsonBeans: %s", applicationName, jsonBeans);
    }

    private static class JsonBean {

        private String someString;
        private int someInt;

        @Override
        public String toString() {
            return "JsonBean{" +
                    "someString='" + someString + '\'' +
                    ", someInt=" + someInt +
                    '}';
        }
    }
}
